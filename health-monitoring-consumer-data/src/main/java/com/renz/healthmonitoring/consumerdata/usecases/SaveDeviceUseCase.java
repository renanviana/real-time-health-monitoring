package com.renz.healthmonitoring.consumerdata.usecases;

import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

public interface SaveDeviceUseCase {
    
    void saveIfAbsent(Device device);

}
