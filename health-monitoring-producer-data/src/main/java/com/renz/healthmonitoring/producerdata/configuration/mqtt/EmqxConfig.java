package com.renz.healthmonitoring.producerdata.configuration.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmqxConfig {

    @Value("${broker.emqx.url}")
    private String brokerUrl;

    @Value("${broker.emqx.client-id}")
    private String clientId;

    @Value("${broker.emqx.timeout}")
    private Integer timeout;

    @Bean
    public IMqttClient emqxClient() throws MqttException {
        IMqttClient emqxClient = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(timeout);
        emqxClient.connect(options);
        return emqxClient;
    }

}
