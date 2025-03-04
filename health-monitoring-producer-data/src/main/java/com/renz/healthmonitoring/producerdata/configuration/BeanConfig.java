package com.renz.healthmonitoring.producerdata.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;

import com.renz.healthmonitoring.producerdata.adapter.DeviceCreator;
import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.adapter.messaging.KafkaDeviceCreator;
import com.renz.healthmonitoring.producerdata.adapter.messaging.KafkaDeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.messaging.KafkaDevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.impl.CreateTopicUseCaseImpl;
import com.renz.healthmonitoring.producerdata.usecases.impl.TransferDataFromDeviceToTopicIUseCaseImpl;

@Configuration
@DependsOn({
        "emqxClient",
        "kafkaTemplate",
        "adminClient" })
public class BeanConfig {

    @Bean
    @Order(1)
    public DevicePublisher devicePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaDevicePublisher(kafkaTemplate);
    }

    @Bean
    @Order(2)
    public DeviceCreator deviceCreator(AdminClient adminClient) {
        return new KafkaDeviceCreator(adminClient);
    }

    @Bean
    @Order(3)
    public DeviceInformer deviceInformer(AdminClient adminClient) {
        return new KafkaDeviceInformer(adminClient);
    }

    @Bean
    @Order(4)
    public CreateTopicUseCase createTopicUseCase(DeviceCreator deviceCreator, DeviceInformer deviceInformer) {
        return new CreateTopicUseCaseImpl(deviceCreator, deviceInformer);
    }

    @Bean
    @Order(5)
    public TransferDataFromDeviceToTopicUseCase transferDataFromDeviceToTopicUseCase(
            IMqttClient emqxClient,
            DevicePublisher devicePublisher,
            CreateTopicUseCase createTopicUseCase,
            DeviceInformer deviceInformer) {
        return new TransferDataFromDeviceToTopicIUseCaseImpl(
                emqxClient,
                devicePublisher,
                createTopicUseCase,
                deviceInformer);
    }

}
