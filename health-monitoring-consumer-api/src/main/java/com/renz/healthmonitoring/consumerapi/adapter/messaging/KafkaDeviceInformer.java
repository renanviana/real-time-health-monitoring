package com.renz.healthmonitoring.consumerapi.adapter.messaging;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.stereotype.Component;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceInformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDeviceInformer implements DeviceInformer {

    private final AdminClient adminClient;

    @Override
    public Set<String> getTopicNames() {
        try {
            return adminClient.listTopics().names().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            return new HashSet<String>();
        }
    }

}
