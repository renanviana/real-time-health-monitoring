package com.renz.healthmonitoring.consumerdata.adapter;

import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;

public interface RegistryRepository {
    
    void save(Registry registry);

}
