package com.renz.healthmonitoring.consumerdata.configuration.persistence;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.datastax.oss.driver.api.core.CqlSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@DependsOn("kafkaTopicConfig")
public class CassandraConfig {

    @Value("${spring.data.cassandra.local-datacenter}")
    private String datacenter;

    @Value("${spring.data.cassandra.host}")
    private String host;

    @Value("${spring.data.cassandra.port}")
    private Integer port;

    private final Set<String> topicNames;

    public CassandraConfig(Set<String> topicNames) {
        this.topicNames = topicNames;
    }

    @Bean
    public CqlSession cqlSession() {
        CqlSession cqlSession = CqlSession.builder()
                .withLocalDatacenter(datacenter)
                .addContactPoint(new InetSocketAddress(host, port))
                .build();
        try {
            createKeyspaceAndDeviceTables(cqlSession);
            createRegistryTables(cqlSession);
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return cqlSession;
    }

    private void createKeyspaceAndDeviceTables(CqlSession cqlSession) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("cassandra/createKeyspace.cql").toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        StringBuilder commandBuffer = new StringBuilder();
        List<String> queries = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*") || line.startsWith("*")) {
                continue;
            }
            commandBuffer.append(line).append(" ");
            if (line.endsWith(";")) {
                queries.add(commandBuffer.toString().trim());
                commandBuffer.setLength(0);
            }
        }
        for (String query : queries) {
            cqlSession.execute(query);
            log.info("Cassandra query executed: {}", query);
        }
    }

    private void createRegistryTables(CqlSession cqlSession) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path pathcreateRegistryTableCql = Paths
                .get(classLoader.getResource("cassandra/createRegistryTable.cql").toURI());
        String createRegistryTableCql = Files.readString(pathcreateRegistryTableCql, StandardCharsets.UTF_8);
        topicNames.forEach(topic -> {
            String script = createRegistryTableCql.replace("${table_name}", topic);
            cqlSession.execute(script);
        });
    }

}
