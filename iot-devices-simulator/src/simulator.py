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
    "hr": {"name": "heart rate", "unit": "bpm", "min": 60, "max": 100, "type": int},
    "temp": {"name": "temperature", "unit": "degrees", "min": 36.0, "max": 37.5, "type": float},
    "spO2": {"name": "oxygen saturation", "unit": "percentage", "min": 95, "max": 100, "type": int},
    "bpSys": {"name": "blood pressure systolic", "unit": "mmHg", "min": 90, "max": 140, "type": int},
    "bpDia": {"name": "blood pressure diastolic", "unit": "mmHg", "min": 60, "max": 90, "type": int},
}

devices = {}    # {device_id: device}
running = {}    # {device_id: bool}

def publish_new_sensor(sensor_type):
    sensor = sensors[sensor_type]
    device =  {
        "id": str(uuid.uuid4()),
        "name": sensor["name"],
        "type": sensor_type
    }
    start_thread(device)
    return device

def publish_data(device):
    try:
        client = mqtt.Client()

        topic = f"{TOPIC}/{device["type"]}/{device["id"]}"
        print(f"[{time.time()}] Device {device["id"]}: Publishing to topic {topic}")
        
        if connect_with_retries(client):
            client.loop_start()  # keep client in execution to publish messages
            while running.get(device["id"], False):
                device_data = generate_device_data(device)
                payload = json.dumps(device_data)
                result = client.publish(topic, payload, qos=1)
                status = result[0]
                if status == 0:
                    print(f"[{time.time()}] Device {device["id"]}: Sent `{payload}` successfully.")
                else:
                    print(f"[{time.time()}] Device {device["id"]}: Failed to send message (status: {status})")
                time.sleep(1)
        else:
            print("Could not connect to MQTT Broker. Exiting.")
    except Exception as e:
        print(f"[{time.time()}] Error with device {device["id"]}: {e}")

def generate_device_data(device):
    sensor = sensors[device["type"]]
    value = round(random.uniform(sensor["min"], sensor["max"]), 2)
    if sensor["type"] == int:
        value = int(value)
    return {
        "id": device["id"],
        "name": sensor["name"],
        "type": device["type"],
        "timestamp": int(time.time()),
        "unit": sensor["unit"],
        "value": value
    }

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
    if device["id"] in running and running[device["id"]]:
        return
    running[device["id"]] = True
    devices[device["id"]] = device
    thread = threading.Thread(target=publish_data, args=(device,))
    thread.daemon = True
    thread.start()

def stop_thread(device_id):
    if device_id in list(running.keys()):
        running[device_id] = False

def stop_all():
    for device_id in list(running.keys()):
        stop_thread(device_id)

def list_devices():
    results = []
    for device_id in list(running.keys()):
        if running[device_id]:
            results.append(devices[device_id])
    return results
