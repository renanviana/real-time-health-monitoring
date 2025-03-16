function createTemperatureChart(deviceId) {
  const showLastSeconds = 8000;
  let currentValue = 0;
  let lastTimestamp = Date.now();
  let sseConnected = false;
  let eventSource = null;

  const ctx = document.getElementById(deviceId).getContext("2d");

  const chart = new Chart(ctx, {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "Temperatura Corporal (°C)",
          data: [],
          borderColor: "rgba(255, 99, 132, 1)",
          borderWidth: 2,
          tension: 0.3,
          fill: false,
          pointRadius: 3,
        },
      ],
    },
    options: {
      animation: false,
      responsive: true,
      interaction: {
        mode: "nearest",
        intersect: false,
      },
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
          min: () => lastTimestamp - showLastSeconds,
          max: () => lastTimestamp,
          ticks: {
            autoSkip: true,
            maxTicksLimit: 8,
          },
        },
        y: {
          min: 35,
          max: 40,
          title: { display: true, text: "Temperatura (°C)" },
          ticks: {
            stepSize: 1,
            callback: function (value) {
              return value.toFixed(1);
            },
          },
        },
      },
    },
  });

  function updateChart() {
    if (!sseConnected) return;

    const newPoint = currentValue;
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
    eventSource = new EventSource(`${URL_CONSUMER_API}/temp/${deviceId}`);

    eventSource.onopen = () => {
      console.log("SSE Conectado.");
      sseConnected = true;
    };

    eventSource.onmessage = (event) => {
      const data = JSON.parse(JSON.parse(event.data));
      lastTimestamp = data.temp.timestamp * 1000;
      currentValue = data.temp.value;
      updateChart();
    };

    eventSource.onerror = (error) => {
      console.error("Erro SSE:", error);
      eventSource.close();
      sseConnected = false;
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
}
