/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.rcll.java;

import com.rcll.protobuf_lib.RobotConnections;
import com.rcll.refbox.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public class App {


    public static void main(String[] args) throws MqttException, InterruptedException {
        String publisherId = UUID.randomUUID().toString();
        IMqttClient publisher = new MqttClient("tcp://localhost:1883",publisherId);
        publisher.connect();
        RefboxConnectionConfig connectionConfig = new RefboxConnectionConfig(
                "172.26.105.14",
                new PeerConfig(4444, 4445),
                new PeerConfig(4441, 4446),
                new PeerConfig(4442, 4447));
        TeamConfig teamConfig = new TeamConfig("randomkey", "GRIPS");
        RefboxHandler privateHandler = new RefboxHandler();
        RefboxHandler publicHandler = new RefboxHandler();
        RefboxMqttHandler privateMqttHandler = new RefboxMqttHandler(privateHandler, "private", publisher);
        RefboxMqttHandler publicMqttHandler = new RefboxMqttHandler(publicHandler, "public", publisher);
        RefboxClient refboxClient = new RefboxClient(connectionConfig, teamConfig, privateHandler, publicHandler, 2000);
        refboxClient.startServer();
        while (publisher.isConnected()) {
            Thread.sleep(1000);
        }
    }
}
