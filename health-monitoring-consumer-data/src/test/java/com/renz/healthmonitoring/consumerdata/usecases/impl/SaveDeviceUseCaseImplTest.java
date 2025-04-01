package com.renz.healthmonitoring.consumerdata.usecases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

@ExtendWith(MockitoExtension.class)
class SaveDeviceUseCaseImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private SaveDeviceUseCaseImpl saveDeviceUseCase;

    @Test
    void shouldNotSaveDeviceIfAlreadyExists() {
        Device device = new Device("123", "TypeX");
        when(deviceRepository.findById("123")).thenReturn(Optional.of(device));
        saveDeviceUseCase.saveIfAbsent(device);
        verify(deviceRepository, never()).save(any());
    }

    @Test
    void shouldSaveDeviceIfNotExistsAndCleanType() {
        Device device = new Device("123", "\"TypeX\"");
        when(deviceRepository.findById("123")).thenReturn(Optional.empty());
        saveDeviceUseCase.saveIfAbsent(device);
        assertEquals("TypeX", device.getType());
        verify(deviceRepository, times(1)).save(device);
    }

}
