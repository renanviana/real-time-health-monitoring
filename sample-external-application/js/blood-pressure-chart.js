function createBloodPressureChart(deviceId) {
  const showLastSeconds = 8000;
  let currentValueBpSys = 0;
  let currentValueBpDia = 0;
  let lastTimestamp = Date.now();
  let sseConnected = false;
  let eventSource = null;

  const ctx = document.getElementById(deviceId).getContext("2d");

  const chart = new Chart(ctx, {
    type: "scatter",
    data: {
      datasets: [
        {
          label: "Pressão Arterial Sistólica (mmHg)",
          data: [],
          backgroundColor: "rgba(153, 102, 255, 1)",
          pointRadius: 5,
        },
        {
          label: "Pressão Arterial Diastólica (mmHg)",
          data: [],
          backgroundColor: "rgba(255, 159, 64, 1)",
          pointRadius: 5,
        },
      ],
    },
    options: {
      responsive: true,
      animation: false,
      plugins: {
        tooltip: {
          callbacks: {
            label: function (tooltipItem) {
              return (
                tooltipItem.dataset.label + ": " + tooltipItem.raw.y + " mmHg"
              );
            },
          },
        },
      },
      scales: {
        x: {
          type: "time",
          time: {
            unit: "second",
            tooltipFormat: "HH:mm:ss",
            displayFormats: {
              second: "HH:mm:ss",
            },
          },
          title: { display: true, text: "Tempo" },
          min: () => Date.now() - showLastSeconds,
          max: () => Date.now(),
        },
        y: {
          min: 50,
          max: 150,
          title: { display: true, text: "Pressão Arterial (mmHg)" },
        },
      },
    },
  });

  function updateChart() {
    if (!sseConnected) return;
    const now = lastTimestamp;

    chart.data.datasets[0].data.push({ x: now, y: currentValueBpSys });
    chart.data.datasets[1].data.push({ x: now, y: currentValueBpDia });

    const cutoffTime = now - showLastSeconds;
    chart.data.datasets.forEach((dataset) => {
      dataset.data = dataset.data.filter((point) => point.x >= cutoffTime);
    });

    chart.update();
  }

  function startEventSource() {
    eventSource = new EventSource(`${URL_CONSUMER_API}/bloodP/${deviceId}`);

    eventSource.onopen = () => {
      console.log("SSE Conectado.");
      sseConnected = true;
    };

    eventSource.onmessage = (event) => {
      const data = JSON.parse(JSON.parse(event.data));
      lastTimestamp = data.bpSys.timestamp;
      currentValueBpSys = data.bpSys.value;
      currentValueBpDia = data.bpDia.value;
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
