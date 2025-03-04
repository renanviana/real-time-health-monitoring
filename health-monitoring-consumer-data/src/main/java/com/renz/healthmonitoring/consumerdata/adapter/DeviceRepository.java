package com.renz.healthmonitoring.consumerdata.adapter;

import java.util.List;
import java.util.Optional;

import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

public interface DeviceRepository {
    
    Device save(Device device);

    List<Device> findAll();
    
    Optional<Device> findById(String id);

}
