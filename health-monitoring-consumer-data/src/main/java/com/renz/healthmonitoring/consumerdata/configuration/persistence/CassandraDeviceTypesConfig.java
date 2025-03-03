package com.renz.healthmonitoring.consumerdata.configuration.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;

@Configuration
@DependsOn({
        "cassandraConfig",
        "kafkaTopicConfig"
})
public class CassandraDeviceTypesConfig {

    @Bean
    public List<Device> deviceTypes(Set<String> topicNames, DeviceRepository deviceRepository) {
        topicNames.forEach(topic -> {
            System.out.println("#### " + topic);
            String[] topicSplited = topic.split("_");
            if (topicSplited.length == 2) {
                String deviceName = topicSplited[0];
                String id = topicSplited[1];
                Device deviceEntity = new Device(id, deviceName);
                deviceRepository.save(deviceEntity);
            }
        });
        return deviceRepository.findAll();
    }
}
