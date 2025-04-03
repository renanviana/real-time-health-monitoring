from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from simulator import list_devices
from simulator import publish_new_sensor
from simulator import stop_thread
from simulator import reactivate_thread

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class Device(BaseModel):
    type: str

@app.get("/simulator-devices")
def get_devices():
    return { "devices": list_devices() }

@app.post("/simulator-devices")
async def post_devices(device: Device):
    device = publish_new_sensor(device.type)
    return { "device": device }

@app.delete("/simulator-devices/{device_id}")
async def active_devices_id(device_id: str):
    stop_thread(device_id)
    return { "message": f"Device {device_id} unsubscribed from EMQX" }

@app.put("/simulator-devices/{device_id}")
async def reactivate_devices_id(device_id: str):
    reactivate_thread(device_id)
    return { "message": f"Device {device_id} reactive on EMQX" }
