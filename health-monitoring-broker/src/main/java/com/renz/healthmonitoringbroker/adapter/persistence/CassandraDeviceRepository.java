package com.renz.healthmonitoringbroker.adapter.persistence;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.renz.healthmonitoringbroker.adapter.DeviceRepository;
import com.renz.healthmonitoringbroker.domain.entity.cassandra.Device;

@Repository
public interface CassandraDeviceRepository extends DeviceRepository, CassandraRepository<Device, String> {
    
}
