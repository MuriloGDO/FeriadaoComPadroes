package com.robot.mr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.robot.mr.adapter.ESP32Adapter;
import com.robot.mr.interfaces.IRobot;
import com.robot.mr.proxy.CameraProxy;
import com.robot.mr.singleton.WifiConnectionManager;

import jakarta.annotation.PostConstruct;

@Configuration
public class RobotConfig {
 
    @Value("${robot.esp32.host:192.168.1.100}")
    private String esp32Host;
 
    @Value("${robot.esp32.port:8080}")
    private int esp32Port;
 
    @PostConstruct
    public void configureWiFiManager() {
        WifiConnectionManager.getInstance().configure(esp32Host, esp32Port);
    }
 
    @Bean
    @Primary
    public IRobot robotAdapter(ESP32Adapter esp32Adapter) {
        return esp32Adapter;
    }
 
    @Bean
    public CameraProxy cameraProxy(IRobot robot) {
        return new CameraProxy(robot);
    }
}