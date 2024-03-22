package com.rcll.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcll.domain.*;
import com.rcll.refbox.RefboxClient;
import lombok.extern.apachecommons.CommonsLog;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@CommonsLog
public class RefboxTeamHandler implements MqttCallback {
    private final IMqttClient mqttClient;
    private final RefboxClient refboxClient;

    private final ObjectMapper objectMapper;
    private final Map<String, Consumer<String>> callbacks;
    private final String prepareBsInputTopic;
    private final String prepareBsOutputTopic;

    private final String prepareCs1Topic;
    private final String prepareCs2Topic;
    private final String prepareDsTopic;
    private final String prepareRs1Topic;
    private final String prepareRs2Topic;

    private final String beaconRobot1Topic;
    private final String beaconRobot2Topic;
    private final String beaconRobot3Topic;

    public RefboxTeamHandler(IMqttClient mqttClient, RefboxClient refboxClient, String teamName) {
        this.mqttClient = mqttClient;
        this.refboxClient = refboxClient;
        this.mqttClient.setCallback(this);
        this.callbacks = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        prepareBsInputTopic = teamName + "/prepare/BS/input";
        prepareBsOutputTopic = teamName + "/prepare/BS/output";
        prepareCs1Topic = teamName + "/prepare/CS1";
        prepareCs2Topic = teamName + "/prepare/CS2";
        prepareDsTopic = teamName + "/prepare/DS";
        prepareRs1Topic = teamName + "/prepare/RS1";
        prepareRs2Topic = teamName + "/prepare/RS2";
        beaconRobot1Topic = teamName + "/beacon/R1";
        beaconRobot2Topic = teamName + "/beacon/R2";
        beaconRobot3Topic = teamName + "/beacon/R3";

        this.callbacks.put(prepareBsInputTopic, this::prepareBsInput);
        this.callbacks.put(prepareBsOutputTopic, this::prepareBsOutput);
        this.callbacks.put(prepareCs1Topic, this::prepareCs1);
        this.callbacks.put(prepareCs2Topic, this::prepareCs2);
        this.callbacks.put(prepareDsTopic, this::prepareDs);
        this.callbacks.put(prepareRs1Topic, this::prepareRs1);
        this.callbacks.put(prepareRs2Topic, this::prepareRs2);
        this.callbacks.put(beaconRobot1Topic, (s) -> this.sendRobotBeaconSignal(1, s));
        this.callbacks.put(beaconRobot2Topic, (s) -> this.sendRobotBeaconSignal(2, s));
        this.callbacks.put(beaconRobot3Topic, (s) -> this.sendRobotBeaconSignal(3, s));
    }

    private void prepareRs1(String s) {
        try {
            this.refboxClient.sendPrepareRS(Machine.RS1, RingColor.valueOf(s));
        } catch (Exception ex) {
            log.warn("Error on prepareRs1!", ex);
        }
    }

    private void prepareRs2(String s) {
        try {
            this.refboxClient.sendPrepareRS(Machine.RS2, RingColor.valueOf(s));
        } catch (Exception ex) {
            log.warn("Error on prepareRs2!", ex);
        }
    }

    private void prepareDs(String s) {
        try {
            this.refboxClient.sendPrepareDS(Integer.parseInt(s));
        } catch (Exception ex) {
            log.warn("Error on prepareDs!", ex);
        }
    }

    private void prepareCs1(String s) {
        try {
            this.refboxClient.sendPrepareCS(Machine.CS1, CapStationInstruction.valueOf(s));
        } catch (Exception ex) {
            log.warn("Error on prepareCs1!", ex);
        }
    }

    private void prepareCs2(String s) {
        try {
            this.refboxClient.sendPrepareCS(Machine.CS2, CapStationInstruction.valueOf(s));
        } catch (Exception ex) {
            log.warn("Error on prepareCs2!", ex);
        }
    }

    private void sendRobotBeaconSignal(Integer robotId, String dataStr) {
        try {
            RobotBeaconData data = objectMapper.readValue(dataStr, RobotBeaconData.class);
            this.refboxClient.sendBeaconSignal(robotId, data.getName(), data.getX(), data.getY(), data.getYaw());
        } catch (Exception ex) {
            log.warn("Error on sending Beacon signal for Robot" + robotId + "!", ex);
        }
    }

    private void prepareBsOutput(String s) {
        try {
            this.refboxClient.sendPrepareBS(MachineSide.Output, Base.valueOf(s));
        } catch (Exception ex) {
            log.warn("Error on prepareBsOutput!", ex);
        }
    }

    private void prepareBsInput(String s) {
        try {
            this.refboxClient.sendPrepareBS(MachineSide.Input, Base.valueOf(s));
        } catch (Exception ex) {
            log.warn("Error on preparingCs1!", ex);
        }
    }

    public void start() throws MqttException {
        this.mqttClient.subscribe(new String[]{
                prepareBsInputTopic, prepareBsOutputTopic, prepareCs1Topic,
                prepareCs2Topic, prepareDsTopic, prepareRs1Topic, prepareRs2Topic,
                beaconRobot1Topic, beaconRobot2Topic, beaconRobot3Topic});
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("Lost connection to broker! ", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        if (callbacks.containsKey(topic)) {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            this.callbacks.get(topic).accept(payload);
        } else {
            log.warn("Received message on not handeled topic: " + topic);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
