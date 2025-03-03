package com.renz.healthmonitoring.consumerapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.adapter.webflux.WebFluxDeviceHandler;
import com.renz.healthmonitoring.consumerapi.usecases.GetDeviceTypesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByNameUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByDeviceNameAndDeviceIdUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDeviceTypesUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetDevicesByNameUseCaseImpl;
import com.renz.healthmonitoring.consumerapi.usecases.impl.GetRegistersByDeviceNameAndDeviceIdUseCaseImpl;

@Configuration
public class BeanConfig {

    @Bean
    public GetDeviceTypesUseCase getDeviceDataUseCase(DeviceRepository deviceRepository) {
        return new GetDeviceTypesUseCaseImpl(deviceRepository);
    }

    @Bean
    public GetDevicesByNameUseCase getDevicesByNameUseCase(DeviceRepository deviceRepository) {
        return new GetDevicesByNameUseCaseImpl(deviceRepository);
    }

    @Bean
    public GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase(
            DeviceConsumer deviceConsumer) {
        return new GetRegistersByDeviceNameAndDeviceIdUseCaseImpl(deviceConsumer);
    }

    @Bean
    public WebFluxDeviceHandler DeviceHandler(
            GetDeviceTypesUseCase getDeviceTypesUseCase,
            GetDevicesByNameUseCase getDevicesByNameUseCase,
            GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase) {
        return new WebFluxDeviceHandler(getDeviceTypesUseCase, getDevicesByNameUseCase,
                getRegistersByDeviceNameAndDeviceIdUseCase);
    }

}
