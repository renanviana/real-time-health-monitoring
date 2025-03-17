const URL_IOT_DEVICES_SIMULATOR = "http://localhost:8000/simulator-devices";
const URL_CONSUMER_API = "http://localhost:8080";
const deviceIdList = [];
let eventSource = null;
let streamData = {};
let factoryMap = {};

const chartFactories = {
  ecg: (id) => ecgChartFactory(id),
  temp: (id) => tempChartFactory(id),
  spO2: (id) => spO2ChartFactory(id),
  bloodP: (id) => bloodPChartFactory(id),
};

const setStreamData = {
  ecg: (data) => {
    const deviceId = data.ecg.id;
    if (!streamData[deviceId]) {
      streamData[deviceId] = [];
    }
    streamData[deviceId].push(data.ecg);
    const chartFactory = factoryMap[deviceId];
    chartFactory.updateChart();
  },
  temp: (data) => {
    const deviceId = data.temp.id;
    if (!streamData[deviceId]) {
      streamData[deviceId] = [];
    }
    streamData[deviceId].push(data.temp);
    const chartFactory = factoryMap[deviceId];
    chartFactory.updateChart();
  },
  spO2: (data) => {
    const deviceId = data.spO2.id;
    if (!streamData[deviceId]) {
      streamData[deviceId] = [];
    }
    streamData[deviceId].push(data.spO2);
    const chartFactory = factoryMap[deviceId];
    chartFactory.updateChart();
  },
  bloodP: (data) => {
    const deviceId = data.bpSys.id;
    if (!streamData[deviceId]) {
      streamData[deviceId] = [];
    }
    streamData[deviceId].push({ bpSys: data.bpSys, bpDia: data.bpDia });
    const chartFactory = factoryMap[deviceId];
    chartFactory.updateChart();
  },
};

window.addEventListener("beforeunload", () => {
  if (eventSource) {
    eventSource.close();
    console.log("SSE fechado antes de sair da página.");
  }
});

function startEventSource() {
  if (eventSource === null) {
    eventSource = new EventSource(`${URL_CONSUMER_API}/stream`);

    eventSource.onopen = () => {
      console.log("SSE Conectado.");
    };

    eventSource.onerror = (error) => {
      console.error("Erro SSE:", error);
      eventSource.close();
      eventSource = null;
      streamData = {};
      setTimeout(startEventSource, 5000);
    };

    eventSource.onmessage = (event) => {
      const data = JSON.parse(JSON.parse(event.data));
      let deviceType = null;
      if (data["ecg"]) {
        deviceType = data["ecg"]["device_type"];
      }
      if (data["bpSys"]) {
        deviceType = data["bpSys"]["device_type"];
      }
      if (data["spO2"]) {
        deviceType = data["spO2"]["device_type"];
      }
      if (data["temp"]) {
        deviceType = data["temp"]["device_type"];
      }
      setStreamData[deviceType](data);
    };
  }
}

function populateCharts() {
  fetch(URL_IOT_DEVICES_SIMULATOR)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      const devices = data.devices;
      Array.from(devices).forEach((device) => {
        if (!deviceIdList.includes(device.id)) {
          deviceIdList.push(device.id);
          const div = document.createElement("div");
          div.className = "chart-item";
          div.id = `chart-item-${device.id}`;
          div.innerHTML = `
                <canvas id="${device.id}" class="${device.type}-chart"></canvas>
            `;
          chartsContainer.appendChild(div);
          const chartFactory = chartFactories[device.type](device.id);
          factoryMap[device.id] = chartFactory;
          chartFactory.createChart();
          chartFactory.updateChart();
        }
      });
      startEventSource();
    })
    .catch((error) => console.error("Erro:", error));
}

function populateDeviceTable() {
  const tableBody = document.getElementById("deviceTableBody");
  tableBody.innerHTML = "";

  fetch(URL_IOT_DEVICES_SIMULATOR)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      const devices = data.devices;
      Array.from(devices).forEach((device) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${device.id}</td>
            <td>${device.name}</td>
            <td>${device.type}</td>
            <td class="actions">
                <button class="btn-excluir" onclick="deleteDevice('${device.id}')">Excluir</button>
            </td>
        `;
        tableBody.appendChild(row);
      });
    })
    .catch((error) => console.error("Erro:", error));
}

function deleteDevice(id) {
  fetch(`${URL_IOT_DEVICES_SIMULATOR}/${id}`, {
    method: "DELETE",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      console.log(`Dispositivo ID=${id} deletado com sucesso!`);
      const index = deviceIdList.indexOf(id);
      if (index !== -1) {
        deviceIdList.splice(index, 1);
      }
      delete streamData[id];
      delete factoryMap[id];
      document.getElementById(`chart-item-${id}`).remove();
      showForm();
    })
    .catch((error) => console.error(`Erro ao deletar ${id}:`, error));
}

function createDevice() {
  const deviceType = document.getElementById("deviceType").value;
  if (deviceType === "") {
    alert("Por favor, selecione um dispositivo!");
    return;
  }
  fetch(URL_IOT_DEVICES_SIMULATOR, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      type: deviceType,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      document.getElementById("deviceType").value = "";
      showForm();
    })
    .catch((error) => console.error("Erro:", error));
}

function refresh() {
  window.location.reload(true);
}

function showForm() {
  document.getElementById("dashboard").style.display = "none";
  document.getElementById("deviceForm").style.display = "block";
  document.getElementById("dashboardLink").classList.remove("active");
  document.getElementById("formLink").classList.add("active");
  populateDeviceTable();
}

function showDashboard() {
  document.getElementById("dashboard").style.display = "block";
  document.getElementById("deviceForm").style.display = "none";
  document.getElementById("dashboardLink").classList.add("active");
  document.getElementById("formLink").classList.remove("active");
  populateCharts();
}

showDashboard();
