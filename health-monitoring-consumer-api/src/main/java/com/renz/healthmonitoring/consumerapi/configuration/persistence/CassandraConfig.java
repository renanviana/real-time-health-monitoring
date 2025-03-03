package com.renz.healthmonitoring.consumerapi.configuration.persistence;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.oss.driver.api.core.CqlSession;

@Configuration
public class CassandraConfig {

    @Value("${spring.data.cassandra.local-datacenter}")
    private String datacenter;

    @Value("${spring.data.cassandra.host}")
    private String host;

    @Value("${spring.data.cassandra.port}")
    private Integer port;

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Bean
    public CqlSession cqlSession() {
        return CqlSession.builder()
                .withLocalDatacenter(datacenter)
                .addContactPoint(new InetSocketAddress(host, port))
                .withKeyspace(keyspace)
                .build();
    }

}
