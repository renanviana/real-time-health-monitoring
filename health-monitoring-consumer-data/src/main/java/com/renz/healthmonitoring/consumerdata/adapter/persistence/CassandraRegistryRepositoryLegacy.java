package com.renz.healthmonitoring.consumerdata.adapter.persistence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Insert registry in table {} : {}", registry.getTableName(), registry.getData());
    }

    @Override
    public void createTable(String topicName) {
        ClassLoader classLoader = getClass().getClassLoader();
        Path pathcreateRegistryTableCql;
        try {
            pathcreateRegistryTableCql = Paths
                    .get(classLoader.getResource("cassandra/createRegistryTable.cql").toURI());
            String createRegistryTableCql = Files.readString(pathcreateRegistryTableCql, StandardCharsets.UTF_8);
            String script = createRegistryTableCql.replace("${table_name}", topicName);
            cqlSession.execute(script);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

}
