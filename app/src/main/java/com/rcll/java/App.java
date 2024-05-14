/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.rcll.java;

import com.rcll.refbox.*;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.cli.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

@CommonsLog
public class App {
    public static void main(String[] args) throws MqttException, InterruptedException, ParseException {

        Options options = new Options();
        options.addRequiredOption("b", "broker", true, "Mqtt Broker host");
        options.addRequiredOption("r", "refbox", true, "Refbox host");
        options.addRequiredOption("t", "team", true, "Name of the Team");
        options.addRequiredOption("k", "key", true, "Secret key of the Team");

        CommandLineParser parser = new DefaultParser();
        CommandLine parsed;
        try {
            parsed = parser.parse(options, args);
        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar mqtt-bridge-0.5-all.jar", options);
            return;
        }

        String publisherId = UUID.randomUUID().toString();
        String brokerUrl = parsed.getOptionValue("b");
        if (!brokerUrl.startsWith("tcp://") && !brokerUrl.contains("://")) {
            log.warn("did you forget to add the protocol prefix i.e \"tcp://\" to the broker url?");
        }
        IMqttClient publisher = new MqttClient(brokerUrl, publisherId);
        try {
            publisher.connect();
        } catch(MqttException me) {
            log.error("Error on connecting to mqtt broker[" + brokerUrl + "] - is it Running? Msg: " + me.getMessage(), me.getCause());
            System.exit(1);
        }
        RefboxConnectionConfig connectionConfig = new RefboxConnectionConfig(
                parsed.getOptionValue("r"),
                new PeerConfig(4444, 4445),
                new PeerConfig(4441, 4446),
                new PeerConfig(4442, 4447));
        TeamConfig teamConfig = new TeamConfig(parsed.getOptionValue("k"), parsed.getOptionValue("t"));
        RefboxHandler privateHandler = new RefboxHandler();
        RefboxHandler publicHandler = new RefboxHandler();
        RefboxMqttHandler privateMqttHandler = new RefboxMqttHandler(privateHandler, "private", publisher);
        RefboxMqttHandler publicMqttHandler = new RefboxMqttHandler(publicHandler, "public", publisher);
        RefboxClient refboxClient = new RefboxClient(connectionConfig, teamConfig, privateHandler, publicHandler, 2000);
        refboxClient.startServer();
        RefboxTeamHandler refboxTeamHandler = new RefboxTeamHandler(publisher, refboxClient, teamConfig.getName());
        refboxTeamHandler.start();
        while (publisher.isConnected()) {
            Thread.sleep(1000);
        }
    }
}
