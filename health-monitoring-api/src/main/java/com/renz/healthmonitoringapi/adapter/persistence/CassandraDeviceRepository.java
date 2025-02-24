package com.renz.healthmonitoringapi.adapter.persistence;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.renz.healthmonitoringapi.adapter.DeviceRepository;
import com.renz.healthmonitoringapi.domain.entity.cassandra.Device;

@Repository
public interface CassandraDeviceRepository extends DeviceRepository, ReactiveCassandraRepository<Device, String> {
    
}
