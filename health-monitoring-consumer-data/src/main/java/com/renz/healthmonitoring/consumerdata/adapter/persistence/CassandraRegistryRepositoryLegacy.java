package com.renz.healthmonitoring.consumerdata.adapter.persistence;

import org.springframework.beans.factory.annotation.Value;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CassandraRegistryRepositoryLegacy implements RegistryRepository {

    private final CqlSession cqlSession;

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

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
        String tableName = "t_" + topicName.replace("-", "_");
        if (notExistsTable(tableName)) {
            this.cqlSession.execute(String.format(
                    "CREATE TABLE %s (uuid TEXT PRIMARY KEY, data TEXT, timestamp BIGINT)",
                    tableName));
            log.info("Created table : {}", tableName);
            this.cqlSession.execute(String.format(
                    "CREATE INDEX IF NOT EXISTS timestamp_idx ON %s (timestamp)", tableName));
            log.info("Created timestamp_idx index : {}", tableName);
        }
    }

    private boolean notExistsTable(String tableName) {
        return !this.cqlSession.getMetadata()
                .getKeyspace(keyspace)
                .flatMap(ks -> ks.getTable(tableName))
                .isPresent();
    }

}
