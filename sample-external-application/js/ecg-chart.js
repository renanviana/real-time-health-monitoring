function createEcgChart(deviceId) {
  const ctx = document.getElementById(deviceId).getContext("2d");

  const showLastSeconds = 30000;
  let currentBPM = 80;
  let lastTimestamp = Date.now();
  let sseConnected = false;
  let eventSource = null;

  function generateECGPoint() {
    const base = (currentBPM / 60) * 100;
    const noise = Math.random() * 10 - 5;
    const isPeak = Math.random() < 0.02;
    const peak = isPeak ? (Math.random() < 0.5 ? 50 : -50) : 0;
    return base + noise + peak;
  }

  const chart = new Chart(ctx, {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "Eletrocardiograma",
          data: [],
          borderColor: "rgba(75, 192, 192, 1)",
          borderWidth: 2,
          fill: false,
          pointRadius: 0,
        },
      ],
    },
    options: {
      animation: false,
      scales: {
        x: {
          type: "time",
          time: {
            unit: "second",
            stepSize: 1,
            tooltipFormat: "HH:mm:ss",
            displayFormats: {
              second: "HH:mm:ss",
            },
          },
          title: { display: true, text: "HH:mm:ss" },
          min: () => Date.now() - showLastSeconds,
          max: () => Date.now(),
          ticks: {
            autoSkip: true,
            maxTicksLimit: 30,
          },
        },
        y: {
          suggestedMin: 50,
          suggestedMax: 150,
          title: { display: true, text: "Sinal ECG" },
        },
      },
    },
  });

  function updateChart() {
    if (!sseConnected) return;

    const newPoint = generateECGPoint();
    const now = lastTimestamp;

    chart.data.labels.push(now);
    chart.data.datasets[0].data.push(newPoint);

    const cutoffTime = now - showLastSeconds;
    const filteredLabels = chart.data.labels.filter(
      (label) => label >= cutoffTime
    );
    const filteredData = chart.data.datasets[0].data.slice(
      -filteredLabels.length
    );

    chart.data.labels = filteredLabels;
    chart.data.datasets[0].data = filteredData;

    chart.update();
  }

  function startEventSource() {
    eventSource = new EventSource(`${URL_CONSUMER_API}/ecg/${deviceId}`);

    eventSource.onopen = () => {
      console.log("SSE Conectado.");
      sseConnected = true;
    };

    eventSource.onmessage = (event) => {
      const data = JSON.parse(JSON.parse(event.data));
      lastTimestamp = data.ecg.timestamp;
      currentBPM = data.ecg.value;
    };

    eventSource.onerror = (error) => {
      console.error("Erro SSE:", error);
      sseConnected = false;
      eventSource.close();
      setTimeout(startEventSource, 5000);
    };
  }

  window.addEventListener("beforeunload", () => {
    if (eventSource) {
      eventSource.close();
      console.log("SSE fechado antes de sair da página.");
    }
  });

  startEventSource();
  setInterval(updateChart, 100);
}
