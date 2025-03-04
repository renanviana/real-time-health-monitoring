package com.renz.healthmonitoring.consumerdata.usecases.impl;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerdata.usecases.SaveDeviceUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveDeviceUseCaseImpl implements SaveDeviceUseCase {
    
    private final DeviceRepository deviceRepository;

    public SaveDeviceUseCaseImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void saveIfAbsent(Device device) {
        if (deviceRepository.findById(device.getId()).isPresent()) {
            return;
        }
        device.setType(device.getType().replaceAll("^\"|\"$", ""));
        deviceRepository.save(device);
        log.info("Device saved: {}", device);
    }

}
