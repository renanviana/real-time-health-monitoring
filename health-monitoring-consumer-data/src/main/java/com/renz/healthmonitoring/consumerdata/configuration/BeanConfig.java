package com.renz.healthmonitoring.consumerdata.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.adapter.messaging.KafkaDeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.messaging.KafkaDeviceInformer;
import com.renz.healthmonitoring.consumerdata.adapter.persistence.CassandraRegistryRepositoryLegacy;
import com.renz.healthmonitoring.consumerdata.usecases.SaveDeviceUseCase;
import com.renz.healthmonitoring.consumerdata.usecases.TransferDataFromTopicToDatabaseUseCase;
import com.renz.healthmonitoring.consumerdata.usecases.impl.SaveDeviceUseCaseImpl;
import com.renz.healthmonitoring.consumerdata.usecases.impl.TransferDataFromTopicToDatabaseUseCaseImpl;

@Configuration
@DependsOn({
        "cassandraConfig",
        "kafkaListenerEndpointRegistry",
        "kafkaListenerContainerFactory",
        "messageHandlerMethodFactory",
        "adminClient" })
public class BeanConfig {

    @Bean
    @Order(1)
    public DeviceConsumer deviceConsumer(
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory,
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            DefaultMessageHandlerMethodFactory messageHandlerMethodFactory) {
        return new KafkaDeviceConsumer(
                kafkaListenerContainerFactory,
                kafkaListenerEndpointRegistry,
                messageHandlerMethodFactory);
    }

    @Bean
    @Order(2)
    public RegistryRepository registryRepository(CqlSession cqlSession) {
        return new CassandraRegistryRepositoryLegacy(cqlSession);
    }

    @Bean
    @Order(3)
    public SaveDeviceUseCase saveDeviceUseCase(DeviceRepository deviceRepository) {
        return new SaveDeviceUseCaseImpl(deviceRepository);
    }

    @Bean
    @Order(4)
    public DeviceInformer deviceInformer(AdminClient adminClient) {
        return new KafkaDeviceInformer(adminClient);
    }

    @Bean
    @Order(5)
    public TransferDataFromTopicToDatabaseUseCase transferDataFromTopicToDatabaseUseCase(
            DeviceConsumer deviceConsumer,
            RegistryRepository registryRepository,
            DeviceInformer deviceInformer,
            SaveDeviceUseCase saveDeviceUseCase) {
        return new TransferDataFromTopicToDatabaseUseCaseImpl(
                deviceConsumer,
                registryRepository,
                deviceInformer,
                saveDeviceUseCase);
    }

}
