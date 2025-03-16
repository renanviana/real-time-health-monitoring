function createOxygenSaturationChart(deviceId) {
    const showLastSeconds = 8000;
    let currentValue = 0;
    let lastTimestamp = Date.now();
    let sseConnected = false;
    let eventSource = null;

    const ctx = document.getElementById(deviceId).getContext("2d");

    const chart = new Chart(ctx, {
      type: "bar",
      data: {
        labels: [],
        datasets: [
          {
            label: "Saturação de Oxigênio (Barra)",
            data: [],
            backgroundColor: "rgba(54, 162, 235, 0.2)",
            borderColor: "rgba(54, 162, 235, 1)",
            borderWidth: 1,
            tension: 0,
          },
          {
            label: "Saturação de Oxigênio (Linha)",
            data: [],
            type: "line",
            borderColor: "rgba(54, 162, 235, 1)",
            borderWidth: 2,
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
            min: 0,
            max: 100,
            title: { display: true, text: "Percentual (%)" },
            ticks: {
              stepSize: 1,
              callback: function (value) {
                return value.toFixed(0);
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
      chart.data.datasets[1].data.push(newPoint);

      const cutoffTime = now - showLastSeconds;
      const filteredLabels = chart.data.labels.filter(
        (label) => label >= cutoffTime
      );
      const filteredBarData = chart.data.datasets[0].data.slice(
        -filteredLabels.length
      );
      const filteredLineData = chart.data.datasets[1].data.slice(
        -filteredLabels.length
      );

      chart.data.labels = filteredLabels;
      chart.data.datasets[0].data = filteredBarData;
      chart.data.datasets[1].data = filteredLineData;

      chart.update();
    }

    function startEventSource() {
      eventSource = new EventSource(
        `${URL_CONSUMER_API}/spO2/${deviceId}`
      );

      eventSource.onopen = () => {
        console.log("SSE Conectado.");
        sseConnected = true;
      };

      eventSource.onmessage = (event) => {
        const data = JSON.parse(JSON.parse(event.data));
        lastTimestamp = data.spO2.timestamp * 1000;
        currentValue = data.spO2.value;
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
