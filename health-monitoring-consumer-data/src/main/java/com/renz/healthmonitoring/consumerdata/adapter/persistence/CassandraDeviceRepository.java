package com.renz.healthmonitoring.consumerdata.adapter.persistence;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

@Repository
public interface CassandraDeviceRepository extends DeviceRepository, CassandraRepository<Device, String> {
    
}
