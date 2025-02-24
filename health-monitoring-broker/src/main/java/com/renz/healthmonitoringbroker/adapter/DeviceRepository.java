package com.renz.healthmonitoringbroker.adapter;

import com.renz.healthmonitoringbroker.domain.entity.cassandra.Device;

public interface DeviceRepository {
    
    Device save(Device device);

}
