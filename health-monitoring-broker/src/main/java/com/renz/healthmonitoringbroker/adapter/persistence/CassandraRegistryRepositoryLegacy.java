package com.renz.healthmonitoringbroker.adapter.persistence;

import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoringbroker.adapter.RegistryRepository;
import com.renz.healthmonitoringbroker.domain.entity.cassandra.Registry;

@Repository
public class CassandraRegistryRepositoryLegacy extends CassandraRepositoryLegacy implements RegistryRepository {

    private final CqlSession cqlSession;

    public CassandraRegistryRepositoryLegacy(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    @Override
    public void save(Registry registry) {
        setRandomUUID(registry);
        registry.setTimestamp(System.currentTimeMillis());
        this.cqlSession.execute(String.format(
            "INSERT INTO %s (uuid, data, timestamp) VALUES (?, ?, ?)", registry.getTableName()),
            registry.getUuid(),
            registry.getData(),
            registry.getTimestamp());
    }
    
}
