package com.renz.healthmonitoring.consumerapi.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceHandler;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.adapter.messaging.KafkaDeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.messaging.KafkaDeviceInformer;
import com.renz.healthmonitoring.consumerapi.adapter.webflux.WebFluxDeviceHandler;
import com.renz.healthmonitoring.consumerapi.usecases.CreateKafkaListenersUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByTypeUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.impl.CreateKafkaListenersUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDevicesByTypeUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDevicesUseCaseImpl;

@Configuration
@DependsOn({
        "kafkaListenerEndpointRegistry",
        "kafkaListenerContainerFactory",
        "messageHandlerMethodFactory",
        "adminClient"
})
public class BeanConfig {

    @Bean
    @Order(1)
    public DeviceConsumer deviceConsumer(
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory,
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            DefaultMessageHandlerMethodFactory messageHandlerMethodFactory) {
        return new KafkaDeviceConsumer(
                kafkaListenerContainerFactory,
                kafkaListenerEndpointRegistry,
                messageHandlerMethodFactory);
    }

    @Bean
    @Order(2)
    public DeviceInformer deviceInformer(AdminClient adminClient) {
        return new KafkaDeviceInformer(adminClient);
    }

    @Bean
    @Order(3)
    public GetDevicesUseCase getDevicesUseCase(DeviceRepository deviceRepository) {
        return new GetDevicesUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(4)
    public GetDevicesByTypeUseCase getDevicesByTypeUseCase(DeviceRepository deviceRepository) {
        return new GetDevicesByTypeUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(5)
    public CreateKafkaListenersUseCase createKafkaListenersUseCase(
            DeviceInformer deviceInformer,
            DeviceConsumer deviceConsumer) {
        return new CreateKafkaListenersUseCaseImpl(
                deviceInformer,
                deviceConsumer);
    }

    @Bean
    @Order(6)
    public DeviceHandler deviceHandler(
            GetDevicesUseCase getDevicesUseCase,
            GetDevicesByTypeUseCase getDevicesByTypeUseCase,
            CreateKafkaListenersUseCase createKafkaListenersUseCase) {
        return new WebFluxDeviceHandler(
                getDevicesUseCase,
                getDevicesByTypeUseCase,
                createKafkaListenersUseCase);
    }

}
