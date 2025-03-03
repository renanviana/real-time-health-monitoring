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
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@DependsOn("devicesConfig")
public class CassandraConfig {

    @Value("${spring.data.cassandra.local-datacenter}")
    private String datacenter;

    @Value("${spring.data.cassandra.host}")
    private String host;

    @Value("${spring.data.cassandra.port}")
    private Integer port;

    @Bean
    @Order(1)
    public CqlSession cqlSession(Map<String, String[]> devices) {
        CqlSession cqlSession = CqlSession.builder()
                .withLocalDatacenter(datacenter)
                .addContactPoint(new InetSocketAddress(host, port))
                .build();
        try {
            // create keyspace and devices table
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
            // create registry tables
            Path pathcreateRegistryTableCql = Paths
                    .get(classLoader.getResource("cassandra/createRegistryTable.cql").toURI());
            String createRegistryTableCql = Files.readString(pathcreateRegistryTableCql, StandardCharsets.UTF_8);
            devices.entrySet().forEach(device -> {
                String deviceName = device.getKey();
                String[] deviceIds = device.getValue();
                for (String id : deviceIds) {
                    String tableName = deviceName.concat("_").concat(id);
                    String script = createRegistryTableCql.replace("${table_name}", tableName);
                    cqlSession.execute(script);
                }
            });
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return cqlSession;
    }

    @Bean
    @Order(2)
    public List<Device> cassandraDevices(Map<String, String[]> devices, DeviceRepository deviceRepository) {
        List<Device> cassandraDevices = new ArrayList<Device>();
        devices.entrySet().forEach(device -> {
            String deviceName = device.getKey();
            String[] deviceIds = device.getValue();
            for (String id : deviceIds) {
                Device deviceEntity = new Device(id, deviceName);
                deviceRepository.save(deviceEntity);
                cassandraDevices.add(deviceEntity);
            }
        });
        return cassandraDevices;
    }
}
