package com.renz.healthmonitoring.consumerapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceHandler;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.adapter.messaging.KafkaDeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.webflux.WebFluxDeviceHandler;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByTypeUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByTypeAndIdUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDevicesUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDevicesByTypeUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetRegistersByTypeAndIdUseCaseImpl;

@Configuration
@DependsOn({
        "kafkaListenerEndpointRegistry",
        "kafkaListenerContainerFactory",
        "messageHandlerMethodFactory"
})
public class BeanConfig {

    @Bean
    @Order(1)
    public DeviceConsumer deviceConsumer(
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory,
            DefaultMessageHandlerMethodFactory messageHandlerMethodFactory) {
        return new KafkaDeviceConsumer(
                kafkaListenerEndpointRegistry,
                kafkaListenerContainerFactory,
                messageHandlerMethodFactory);
    }

    @Bean
    @Order(2)
    public GetDevicesUseCase getDevicesUseCase(DeviceRepository deviceRepository) {
        return new GetDevicesUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(3)
    public GetDevicesByTypeUseCase getDevicesByTypeUseCase(DeviceRepository deviceRepository) {
        return new GetDevicesByTypeUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(4)
    public GetRegistersByTypeAndIdUseCase getRegistersByTypeAndIdUseCase(
            DeviceConsumer deviceConsumer) {
        return new GetRegistersByTypeAndIdUseCaseImpl(deviceConsumer);
    }

    @Bean
    @Order(5)
    public DeviceHandler deviceHandler(
            GetDevicesUseCase getDevicesUseCase,
            GetDevicesByTypeUseCase getDevicesByTypeUseCase,
            GetRegistersByTypeAndIdUseCase getRegistersByTypeAndIdUseCase) {
        return new WebFluxDeviceHandler(
                getDevicesUseCase,
                getDevicesByTypeUseCase,
                getRegistersByTypeAndIdUseCase);
    }

}
