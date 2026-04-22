package com.robot.mr.singleton;

import java.util.logging.Logger;

//Mantém o estado centralizado da conexão com o ESP32.
public class WifiConnectionManager {
	private static final Logger logger = Logger.getLogger(WifiConnectionManager.class.getName());
	
	private static WifiConnectionManager instance;
	
	private String esp32Host;
	private int esp32Port;
	private boolean connected;
	
	private WifiConnectionManager() {
		this.esp32Host = "";
        this.esp32Port = 0;
        this.connected = false;
	}
	
	public static WifiConnectionManager getInstance() {
		if (instance == null) {
			synchronized (WifiConnectionManager.class) {
				if (instance == null) {
					instance = new WifiConnectionManager();
					logger.info("WiFiConnectionManager inicializado com valores padrão");
				}
			}
		}
		return instance;
	}
	
	public void configure(String host, int port) {
		this.esp32Host = host;
		this.esp32Port = port;
		logger.info("WiFiConnectionManager configurado — host: " + host + " porta: " + port);
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
		
		if (connected) {
            logger.info("ESP32 conectado em " + esp32Host + ":" + esp32Port);
        } else {
            logger.warning("ESP32 desconectado");
        }
	}

	public String getEsp32Host() {
		return esp32Host;
	}

	public void setEsp32Host(String esp32Host) {
		this.esp32Host = esp32Host;
	}

	public int getEsp32Port() {
		return esp32Port;
	}

	public void setEsp32Port(int esp32Port) {
		this.esp32Port = esp32Port;
	}
	
	public String getAddress() {
        return esp32Host + ":" + esp32Port;
    }

	@Override
	public String toString() {
		return "WifiConnectionManager [esp32Host=" + esp32Host + ", esp32Port=" + esp32Port + ", connected=" + connected
				+ "]";
	}	
}
