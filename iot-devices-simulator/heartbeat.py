import random
import json
import paho.mqtt.client as mqtt
import time
from threading import Thread

BROKER = "emqx"
PORT = 1883
TOPIC = "health/heartbeat"
DEVICE_IDS = ["8T4iF", "A8pMD", "eHrUQ"]

# function to simulate heartbeat
def generate_heart_rate():
    return random.randint(60, 100)

# callback to conenction
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"[{time.time()}] Connected to MQTT Broker!")
    else:
        print(f"[{time.time()}] Failed to connect, return code {rc}")

# callback to publication
def on_publish(client, userdata, mid):
    print(f"[{time.time()}] Message {mid} published successfully.")

# try connection with retries
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

# function to publicate data
def publish_data(device_id):
    try:
        client = mqtt.Client()
        client.on_connect = on_connect
        # client.on_publish = on_publish

        topic = f"{TOPIC}_{device_id}"
        print(f"[{time.time()}] Device {device_id}: Publishing to topic {topic}")
        
        # try connection with retries
        if connect_with_retries(client):
            client.loop_start()  # keep client in execution to publish messages
            while True:
                heart_rate = generate_heart_rate()
                payload = json.dumps({
                    "deviceId": device_id,
                    "timestamp": int(time.time()),
                    "unit": "bpm",
                    "heartRate": heart_rate
                })

                # print(f"[{time.time()}] Device {device_id}: Sending payload {payload}")
                result = client.publish(topic, payload, qos=1)
                status = result[0]

                if status == 0:
                    print(f"[{time.time()}] Device {device_id}: Sent `{payload}` successfully.")
                else:
                    print(f"[{time.time()}] Device {device_id}: Failed to send message (status: {status})")
                # frequency message sending
                time.sleep(1)
        else:
            print("Could not connect to MQTT Broker. Exiting.")
    except Exception as e:
        print(f"[{time.time()}] Error with device {device_id}: {e}")

# create a thread for each device
threads = []
for device_id in DEVICE_IDS:
    thread = Thread(target=publish_data, args=(device_id,))
    thread.start()
    threads.append(thread)

# waiting execution all threads
for thread in threads:
    thread.join()
