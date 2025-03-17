function tempChartFactory(deviceId) {
  const showLastSeconds = 8000;
  let lastTimestamp = Date.now();
  let chart = null;

  const createChart = () => {
    const ctx = document.getElementById(deviceId).getContext("2d");
    chart = new Chart(ctx, {
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
  };

  const updateChart = () => {
    const dataList = streamData[deviceId];
    if (dataList) {
      const data = dataList[dataList.length - 1];
      lastTimestamp = data.timestamp * 1000;
      const newPoint = data.value;
  
      chart.data.labels.push(lastTimestamp);
      chart.data.datasets[0].data.push(newPoint);
  
      const cutoffTime = lastTimestamp - showLastSeconds;
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
  };

  return { createChart, updateChart };

}
