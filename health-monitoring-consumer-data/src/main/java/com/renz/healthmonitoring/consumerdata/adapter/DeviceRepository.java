package com.renz.healthmonitoring.consumerdata.adapter;

import java.util.List;

import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

public interface DeviceRepository {
    
    Device save(Device device);

    List<Device> findAll();

}
