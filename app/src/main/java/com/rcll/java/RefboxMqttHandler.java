package com.rcll.java;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.rcll.domain.TeamColor;
import com.rcll.refbox.RefboxHandler;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.robocup_logistics.llsf_msgs.*;

import java.io.IOException;
import java.util.Optional;


public class RefboxMqttHandler {
    private final RefboxHandler handler;
    private final String prefix;
    private final IMqttClient mqttClient;
    private final TeamColor teamColor;
    private final String teamName;

    public RefboxMqttHandler(RefboxHandler handler, String prefix, IMqttClient mqttClient, TeamColor teamColor,
                             String teamName) {
        this.handler = handler;
        this.prefix = prefix;
        this.mqttClient = mqttClient;
        this.teamColor = teamColor;
        this.teamName = teamName;
        this.handler.setGameStateCallback(this::handleGameState);
        this.handler.setMachineInfoCallback(this::handleMchineInfo);
        this.handler.setOrderInfoCallback(this::handleOrderInfo);
        this.handler.setRingInfoCallback(this::handleRingInfo);
        this.handler.setBeaconSignalCallback(this::handleBeaconSignal);
        this.handler.setExplorationInfoCallback(this::handleExplorationInfo);
        this.handler.setMachineReportInfoCallback(this::handleMachineReportInfo);
        this.handler.setNavigationRoutesCallback(this::handleNavigationRoutes);
        this.handler.setRobotInfoCallback(this::handleRobotInfo);
        this.handler.setVersionInfoCallback(this::handleVersionInfo);
    }

    @SneakyThrows
    private void handleVersionInfo(VersionProtos.VersionInfo versionInfo) {
        this.mqttClient.publish(this.prefix + "/version_info", new MqttMessage(toJson(versionInfo).getBytes()));

    }

    @SneakyThrows
    private void handleRobotInfo(RobotInfoProtos.RobotInfo robotInfo) {
        this.mqttClient.publish(this.prefix + "/robot_info", new MqttMessage(toJson(robotInfo).getBytes()));

    }

    @SneakyThrows
    private void handleNavigationRoutes(NavigationChallengeProtos.NavigationRoutes navigationRoutes) {
        this.mqttClient.publish(this.prefix + "/navigation_routes", new MqttMessage(toJson(navigationRoutes).getBytes()));
    }

    @SneakyThrows

    private void handleMachineReportInfo(MachineReportProtos.MachineReportInfo machineReportInfo) {
        this.mqttClient.publish(this.prefix + "/machine_report_info", new MqttMessage(toJson(machineReportInfo).getBytes()));
    }

    @SneakyThrows

    private void handleExplorationInfo(ExplorationInfoProtos.ExplorationInfo explorationInfo) {
        this.mqttClient.publish(this.prefix + "/exploration_info", new MqttMessage(toJson(explorationInfo).getBytes()));

    }

    @SneakyThrows

    private void handleBeaconSignal(BeaconSignalProtos.BeaconSignal beaconSignal) {
        if (!beaconSignal.hasTeamColor() && !beaconSignal.hasTeamName()) {
            if (teamColor.equals(TeamColor.CYAN)) {
                beaconSignal = beaconSignal.toBuilder().setTeamColor(TeamProtos.Team.CYAN).setTeamName(teamName).build();
            } else {
                beaconSignal = beaconSignal.toBuilder().setTeamColor(TeamProtos.Team.MAGENTA).setTeamName(teamName).build();
            }
        }
        this.mqttClient.publish(this.prefix + "/beacon_signal", new MqttMessage(toJson(beaconSignal).getBytes()));
    }

    @SneakyThrows
    private void handleRingInfo(RingInfoProtos.RingInfo ringInfo) {
        this.mqttClient.publish(this.prefix + "/ring_info", new MqttMessage(toJson(ringInfo).getBytes()));
    }

    @SneakyThrows

    private void handleOrderInfo(OrderInfoProtos.OrderInfo orderInfo) {
        this.mqttClient.publish(this.prefix + "/order_info", new MqttMessage(toJson(orderInfo).getBytes()));
    }

    @SneakyThrows
    private void handleMchineInfo(MachineInfoProtos.MachineInfo machineInfo) {
        this.mqttClient.publish(this.prefix + "/machine_info", toJson(machineInfo).getBytes(), 1, true);
    }

    @SneakyThrows
    private void handleGameState(GameStateProtos.GameState gameState) {
        this.mqttClient.publish(this.prefix + "/game_state", new MqttMessage(toJson(gameState).getBytes()));
    }

    private static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
        return JsonFormat.printer().print(messageOrBuilder);
    }

}
