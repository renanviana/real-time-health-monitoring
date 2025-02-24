package com.renz.healthmonitoringbroker.adapter;

import com.renz.healthmonitoringbroker.domain.entity.cassandra.Registry;

public interface RegistryRepository {
    
    void save(Registry registry);

}
