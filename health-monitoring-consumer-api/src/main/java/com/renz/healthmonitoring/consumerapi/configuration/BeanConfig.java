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
import com.renz.healthmonitoring.consumerapi.usecases.GetDeviceTypesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByNameUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByDeviceNameAndDeviceIdUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDeviceTypesUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDevicesByNameUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetRegistersByDeviceNameAndDeviceIdUseCaseImpl;

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
    public GetDeviceTypesUseCase getDeviceDataUseCase(DeviceRepository deviceRepository) {
        return new GetDeviceTypesUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(3)
    public GetDevicesByNameUseCase getDevicesByNameUseCase(DeviceRepository deviceRepository) {
        return new GetDevicesByNameUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(4)
    public GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase(
            DeviceConsumer deviceConsumer) {
        return new GetRegistersByDeviceNameAndDeviceIdUseCaseImpl(deviceConsumer);
    }

    @Bean
    @Order(5)
    public DeviceHandler deviceHandler(
            GetDeviceTypesUseCase getDeviceTypesUseCase,
            GetDevicesByNameUseCase getDevicesByNameUseCase,
            GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase) {
        return new WebFluxDeviceHandler(getDeviceTypesUseCase, getDevicesByNameUseCase,
                getRegistersByDeviceNameAndDeviceIdUseCase);
    }

}
