package com.renz.healthmonitoring.consumerapi.adapter.persistence;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;

@Repository
public interface CassandraDeviceRepository extends DeviceRepository, ReactiveCassandraRepository<Device, String> {
    
}