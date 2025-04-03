import random
import time
import json
import uuid
import paho.mqtt.client as mqtt
import threading

BROKER = "emqx"
PORT = 1883
TOPIC = "devices"

sensors = {
    "ecg": {
        "name": "ECG", 
        "frequency": 1,
        "values": {
            "ecg": {
                "name": "ECG",
                "unit": "bpm", 
                "min": 60,
                "max": 100, 
                "type": "int"
            }
        }
    },
    "temp": {
        "name": "temperature", 
        "frequency": 1,
        "values": {
            "temp": {
                "name": "temperature",
                "unit": "degrees", 
                "min": 36.0, 
                "max": 37.5, 
                "type": "float"
            }
        }
    },
    "spO2": {
        "name": "oxygen saturation", 
        "frequency": 1,
        "values": {
            "spO2": {
                "name": "oxygen saturation",
                "unit": "percentage", 
                "min": 80,
                "max": 100, 
                "type": "int"
            }
        }
    },
    "bloodP": {
        "name": "blood pressure", 
        "frequency": 1, 
        "values": {
            "bpSys": {
                "name": "blood pressure systolic", 
                "unit": "mmHg", 
                "min": 90, 
                "max": 140, 
                "type": "int"
            },
            "bpDia": {
                "name": "blood pressure diastolic", 
                "unit": "mmHg", 
                "min": 60, 
                "max": 90, 
                "type": "int"
            }
        }
    }
}

devices = {}    # {device_id: device}
running = {}    # {device_id: bool}

def publish_new_sensor(sensor_type):
    sensor = sensors[sensor_type]
    device = {
        "id": str(uuid.uuid4()),
        "name": sensor["name"],
        "type": sensor_type,
        "running": None
    }
    start_thread(device)
    return device

def publish_data(device):
    client = mqtt.Client()
    topic = f"{TOPIC}/{device['type']}/{device['id']}"
    
    print(f"[{time.time()}] Device {device['id']}: Publishing to topic {topic}")

    if not connect_with_retries(client):
        print(f"[{time.time()}] Device {device['id']}: Could not connect to MQTT Broker. Stopping.")
        return

    try:
        client.loop_start()  # keep client in execution to publish messages
        while running.get(device["id"], False):
            device_data = generate_device_data(device)
            payload = json.dumps(device_data)
            result = client.publish(topic, payload, qos=1)
            status = result[0]
            if status == 0:
                print(f"[{time.time()}] Device {device['id']}: Sent `{payload}` successfully.")
            else:
                print(f"[{time.time()}] Device {device['id']}: Failed to send message (status: {status})")
            time.sleep(sensors[device["type"]]["frequency"])
    except Exception as e:
        print(f"[{time.time()}] Error with device {device['id']}: {e}")
    finally:
        client.loop_stop()
        client.disconnect()

def generate_device_data(device):
    sensor = sensors[device["type"]]
    results = {}
    for key, value in sensor["values"].items():
        result = round(random.uniform(value["min"], value["max"]), 2)
        if value["type"] == "int":
            result = int(result)
        results[key] = {
            "id": device["id"],
            "name": value["name"],
            "device_type": device["type"],
            "timestamp": int(time.time()),
            "unit": value["unit"],
            "value": result
        }
    return results

def connect_with_retries(client, retries=10, delay=5):
    for attempt in range(retries):
        try:
            client.connect(BROKER, PORT, 60)
            return True
        except ConnectionRefusedError:
            print(f"Connection refused. Retrying in {delay} seconds... (Attempt {attempt + 1}/{retries})")
            time.sleep(delay)
    print("Failed to connect to MQTT Broker after multiple retries.")
    return False

def start_thread(device):
    if running.get(device["id"], False):
        return
    running[device["id"]] = True
    device["running"] = True
    devices[device["id"]] = device
    thread = threading.Thread(target=publish_data, args=(device,))
    thread.daemon = True
    thread.start()

def stop_thread(device_id):
    if running.get(device_id):
        running[device_id] = False
        devices[device_id]["running"] = False

def list_devices():
    return [devices[device_id] for device_id in running.keys()]

def reactivate_thread(device_id):
    if not running.get(device_id):
        start_thread(devices[device_id])
