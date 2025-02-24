package com.renz.healthmonitoringapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renz.healthmonitoringapi.adapter.DeviceConsumer;
import com.renz.healthmonitoringapi.adapter.DeviceRepository;
import com.renz.healthmonitoringapi.adapter.webflux.WebFluxDeviceHandler;
import com.renz.healthmonitoringapi.usecases.GetDeviceTypesUseCase;
import com.renz.healthmonitoringapi.usecases.GetDevicesByNameUseCase;
import com.renz.healthmonitoringapi.usecases.GetRegistersByDeviceNameAndDeviceIdUseCase;
import com.renz.healthmonitoringapi.usecases.impl.GetDeviceTypesUseCaseImpl;
import com.renz.healthmonitoringapi.usecases.impl.GetDevicesByNameUseCaseImpl;
import com.renz.healthmonitoringapi.usecases.impl.GetRegistersByDeviceNameAndDeviceIdUseCaseImpl;

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
    public GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase(DeviceConsumer deviceConsumer) {
        return new GetRegistersByDeviceNameAndDeviceIdUseCaseImpl(deviceConsumer);
    }

    @Bean
    public WebFluxDeviceHandler DeviceHandler(
            GetDeviceTypesUseCase getDeviceTypesUseCase,
            GetDevicesByNameUseCase getDevicesByNameUseCase, 
            GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase) {
        return new WebFluxDeviceHandler(getDeviceTypesUseCase, getDevicesByNameUseCase, getRegistersByDeviceNameAndDeviceIdUseCase);
    }

}
