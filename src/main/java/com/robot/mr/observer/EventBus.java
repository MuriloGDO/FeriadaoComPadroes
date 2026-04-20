package com.robot.mr.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.robot.mr.enums.EventType;
import com.robot.mr.interfaces.IEventListener;
import com.robot.mr.model.RobotEvent;

//Barramento central de eventos do sistema.
public class EventBus {
	private static final Logger logger = Logger.getLogger(EventBus.class.getName());
	
	private static EventBus instance;
	
	private final Map<EventType, List<IEventListener>> listeners;
	
	// Pool de threads para notificar ouvintes de forma assíncrona,
    // evitando que um ouvinte lento bloqueie o sistema inteiro
    private final ExecutorService executor;

	private EventBus() {
		this.executor = Executors.newCachedThreadPool();
		this.listeners = new ConcurrentHashMap<>();
		
		for (EventType type : EventType.values()) {
			listeners.put(type, new ArrayList<>());
		}
	}
	
	public static EventBus getInstance() {
		if (instance == null) {
			synchronized (EventBus.class) {
				if (instance == null) {
					instance = new EventBus();
					logger.info("EventBus inicializado");
				}
			}
		}
		return instance;
	}
	
	public synchronized void subscribe(EventType type, IEventListener listener) {
		listeners.get(type).add(listener);
		logger.info("Ouvinte registrado para o evento: " + type);
	}
	
	public synchronized void subscribeAll(IEventListener listener) {
		for (EventType type : EventType.values()) {
			listeners.get(type).add(listener);
		}
		logger.info(
				"Ouvinte registrado para todos os eventos: " 
				+ listener.getClass().getSimpleName()
		);
	}
    
	public void publish(RobotEvent event) {
		logger.info(
				"Evento publicado: " + event.getType() 
				+ " | origem: " + event.getSource()
		);
		
		List<IEventListener> targets = listeners.get(event.getType());
		
		if (targets == null || targets.isEmpty()) {
			logger.warning("Nenhum ouvinte registrado para o evento: " + event.getType());
            return;
		}
		
		for (IEventListener listener : targets) {
			executor.submit(() -> {
				try {
					listener.onEvent(event);
				}
				catch (Exception e) {
					logger.severe(
							"Erro ao notificar ouvinte " 
							+ listener.getClass().getSimpleName() 
							+ ": " + e.getMessage()
					);
				}
			});
		}
	}
	
	public synchronized void unsubscribe(EventType type, IEventListener listener) {
		listeners.get(type).remove(listener);
		logger.info("Ouvinte removido do evento: " + type);
	}
	
	public void shutdown() {
		logger.info("Encerrando EventBus...");
        executor.shutdown();
	}
}
