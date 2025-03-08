package com.renz.healthmonitoring.consumerdata.adapter.persistence;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CassandraRegistryRepositoryLegacy implements RegistryRepository {

    private final CqlSession cqlSession;

    @Override
    public void save(Registry registry) {
        registry.setTimestamp(System.currentTimeMillis());
        this.cqlSession.execute(String.format(
                "INSERT INTO %s (uuid, data, timestamp) VALUES (?, ?, ?)", registry.getTableName()),
                registry.getUuid(),
                registry.getData(),
                registry.getTimestamp());
        log.info("Inserted registry on table {} : {}", registry.getTableName(), registry.getData());
    }

    @Override
    public void createTable(String topicName) {
        String tableName = topicName.replace("-", "_");
        this.cqlSession.execute(String.format(
                "CREATE TABLE IF NOT EXISTS t_%s (uuid TEXT PRIMARY KEY, data TEXT, timestamp TIMESTAMP)",
                tableName));
        log.info("Created table : t_{}", tableName);
    }

}
