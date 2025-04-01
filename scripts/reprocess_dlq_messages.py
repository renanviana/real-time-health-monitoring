from kafka import KafkaConsumer, KafkaProducer
from kafka.admin import KafkaAdminClient
import json
import uuid

dlq_topic = 'dead_letter_queue'
bootstrap_servers = ['localhost:9093']

consumer = KafkaConsumer(
    dlq_topic,
    bootstrap_servers=bootstrap_servers,
    auto_offset_reset='earliest',
    enable_auto_commit=True
)

producer = KafkaProducer(
    bootstrap_servers=bootstrap_servers,
    key_serializer=lambda k: k.encode('utf-8') if isinstance(k, str) else k,
    value_serializer=lambda v: v.encode('utf-8') if isinstance(v, str) else v
)

admin_client = KafkaAdminClient(
    bootstrap_servers=bootstrap_servers,
    client_id='reprocess_dlq_messages'
)

messages = consumer.poll(timeout_ms=2000)

last_offsets = {} 

if messages:
    for topic_partition, records in messages.items():
        for message in records:
            try:
                message_decoded = message.value.decode('utf-8')
                json_obj = json.loads(json.loads(message_decoded))
                processing_topic = None
                if 'spO2' in json_obj:
                    processing_topic = json_obj["spO2"]["id"]
                elif 'bpSys' in json_obj:
                    processing_topic = json_obj["bpSys"]["id"]
                elif 'temp' in json_obj:
                    processing_topic = json_obj["temp"]["id"]
                elif 'ecg' in json_obj:
                    processing_topic = json_obj["ecg"]["id"]

                if processing_topic:
                    producer.send(processing_topic, key=str(uuid.uuid4()), value=message_decoded.encode('utf-8'))
                    producer.flush()
                    print(f"Message: {message_decoded} | Sent topic: {processing_topic}")
                
                last_offsets[topic_partition] = message.offset
            except Exception as e:
                print(f"Error to reprocess message: {e}")

    partitions_to_delete = {
        tp: last_offsets[tp] + 1 for tp in last_offsets
    }

    try:
        admin_client.delete_records(partitions_to_delete)
    except Exception as e:
        print(f"Error to clean DLQ: {e}")

consumer.close()
producer.close()
print("Closed connections")
