package com.rcll.java;

import com.google.protobuf.util.JsonFormat;
import com.rcll.domain.*;
import com.rcll.refbox.RefboxClient;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.eclipse.paho.client.mqttv3.*;
import org.robocup_logistics.llsf_msgs.MachineInstructionProtos;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@CommonsLog
public class RefboxTeamHandler implements MqttCallback {
    private final IMqttClient mqttClient;
    private final RefboxClient refboxClient;

    private final String teamName;

    private final Map<String, Consumer<String>> callbacks;
    private final String  prepareBsInputTopic;
    private final String prepareBsOutputTopic;

    private final String prepareCs1Topic;
    private final String prepareCs2Topic;
    private final String prepareDsTopic;
    private final String prepareRs1Topic;
    private final String prepareRs2Topic;

    public RefboxTeamHandler(IMqttClient mqttClient, RefboxClient refboxClient, String teamName) {
        this.mqttClient = mqttClient;
        this.refboxClient = refboxClient;
        this.teamName = teamName;
        this.mqttClient.setCallback(this);
        this.callbacks = new HashMap<>();
        prepareBsInputTopic = teamName + "/prepare/BS/input";
        prepareBsOutputTopic = teamName + "/prepare/BS/output";
        prepareCs1Topic = teamName + "/prepare/CS1";;
        prepareCs2Topic = teamName + "/prepare/CS2";;
        prepareDsTopic = teamName + "/prepare/DS";
        prepareRs1Topic = teamName + "/prepare/RS1";;
        prepareRs2Topic = teamName + "/prepare/RS2";;

        this.callbacks.put(prepareBsInputTopic, this::prepareBsInput);
        this.callbacks.put(prepareBsOutputTopic, this::prepareBsOutput);
        this.callbacks.put(prepareCs1Topic, this::prepareCs1);
        this.callbacks.put(prepareCs2Topic, this::prepareCs2);
        this.callbacks.put(prepareDsTopic, this::prepareDs);
        this.callbacks.put(prepareRs1Topic, this::prepareRs1);
        this.callbacks.put(prepareRs2Topic, this::prepareRs2);
    }

    private void prepareRs1(String s) {
        this.refboxClient.sendPrepareRS(Machine.RS1, RingColor.valueOf(s));
    }

    private void prepareRs2(String s) {
        this.refboxClient.sendPrepareRS(Machine.RS2, RingColor.valueOf(s));
    }

    private void prepareDs(String s) {
        //todo remove gate as soon as java sdk is upgraded to 0.1.17
        this.refboxClient.sendPrepareDS(0, Integer.parseInt(s));
    }

    private void prepareCs1(String s) {
        this.refboxClient.sendPrepareCS(Machine.CS1, CapStationInstruction.valueOf(s));
    }

    private void prepareCs2(String s) {
        this.refboxClient.sendPrepareCS(Machine.CS2, CapStationInstruction.valueOf(s));
    }

    private void prepareBsOutput(String s) {
        this.refboxClient.sendPrepareBS(MachineSide.Output, Base.valueOf(s));
    }

    private void prepareBsInput(String s) {
        this.refboxClient.sendPrepareBS(MachineSide.Input, Base.valueOf(s));
    }

    public void start() throws MqttException {
        this.mqttClient.subscribe(new String[]{prepareBsInputTopic, prepareBsOutputTopic});
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
