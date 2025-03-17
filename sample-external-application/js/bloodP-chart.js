function bloodPChartFactory(deviceId) {
  const showLastSeconds = 8000;
  let lastTimestamp = Date.now();
  let chart = null;

  const createChart = () => {
    const ctx = document.getElementById(deviceId).getContext("2d");
    chart = new Chart(ctx, {
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
            min: () => lastTimestamp - showLastSeconds,
            max: () => lastTimestamp,
          },
          y: {
            min: 50,
            max: 150,
            title: { display: true, text: "Pressão Arterial (mmHg)" },
          },
        },
      },
    });
  };

  const updateChart = () => {
    const dataList = streamData[deviceId];
    if (dataList) {
      const data = dataList[dataList.length - 1];
      lastTimestamp = data.bpSys.timestamp * 1000;
      const now = lastTimestamp;

      chart.data.datasets[0].data.push({ x: now, y: data.bpSys.value });
      chart.data.datasets[1].data.push({ x: now, y: data.bpDia.value });

      const cutoffTime = now - showLastSeconds;
      chart.data.datasets.forEach((dataset) => {
        dataset.data = dataset.data.filter((point) => point.x >= cutoffTime);
      });

      chart.update();
    }
  };

  return { createChart, updateChart };
  
}
