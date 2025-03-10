const URL_IOT_DEVICES_SIMULATOR = "http://localhost:8000/simulator-devices";
const URL_CONSUMER_API = "http://localhost:8080/devices";
const deviceIdList = [];
const eventSources = {};

(function () {
  const OriginalEventSource = window.EventSource;
  window.EventSource = function (url, options) {
    const splitedUrl = url.split("/");
    const id = splitedUrl[splitedUrl.length -1];
    const instance = new OriginalEventSource(url, options);
    eventSources[id] = instance;
    return instance;
  };
  window.EventSource.prototype = OriginalEventSource.prototype;
})();

function populateCharts() {
  const renderFunctions = {
    ecg: (id) => createEcgChart(id),
    temp: (id) => createTemperatureChart(id),
    spO2: (id) => createOxygenSaturationChart(id),
    bloodP: (id) => createBloodPressureChart(id),
  };

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
          const renderChart = renderFunctions[device.type];
          renderChart(device.id);
        }
      });
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
      console.log(`Recurso ${id} deletado com sucesso!`);
      const index = deviceIdList.indexOf(id);
      if (index !== -1) {
        deviceIdList.splice(index, 1);
      }
      const eventSource = eventSources[id];
      eventSource.close();
      delete eventSources[id];
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
