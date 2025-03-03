package com.renz.healthmonitoring.consumerdata.configuration;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.adapter.messaging.KafkaDeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.persistence.CassandraRegistryRepositoryLegacy;
import com.renz.healthmonitoring.consumerdata.usecases.TransferDataFromTopicToDatabase;
import com.renz.healthmonitoring.consumerdata.usecases.impl.TransferDataFromTopicToDatabaseImpl;

@Configuration
@DependsOn({
        "cassandraConfig",
        "topicNames",
        "kafkaListenerEndpointRegistry",
        "kafkaListenerContainerFactory",
        "messageHandlerMethodFactory" })
public class BeanConfig {

    @Bean
    @Order(1)
    public DeviceConsumer deviceConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory,
            DefaultMessageHandlerMethodFactory messageHandlerMethodFactory) {
        return new KafkaDeviceConsumer(
                kafkaListenerEndpointRegistry,
                kafkaListenerContainerFactory,
                messageHandlerMethodFactory);
    }

    @Bean
    @Order(2)
    public RegistryRepository registryRepository(CqlSession cqlSession) {
        return new CassandraRegistryRepositoryLegacy(cqlSession);
    }

    @Bean
    @Order(3)
    public TransferDataFromTopicToDatabase transferDataFromTopicToDatabase(
            DeviceConsumer deviceConsumer,
            RegistryRepository registryRepository,
            Set<String> topicNames) {
        return new TransferDataFromTopicToDatabaseImpl(deviceConsumer, registryRepository, topicNames);
    }

}
