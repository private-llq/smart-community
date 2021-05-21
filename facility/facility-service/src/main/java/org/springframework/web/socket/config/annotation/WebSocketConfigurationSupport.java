package org.springframework.web.socket.config.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerMapping;

/**
 * @author chq459799974
 * @description TODO
 * @since 2021-05-19 11:50
 **/
public class WebSocketConfigurationSupport {
	@Nullable
	private ServletWebSocketHandlerRegistry handlerRegistry;
	@Nullable
	private TaskScheduler scheduler;
	
	public WebSocketConfigurationSupport() {
	}
	
	@Bean
	public HandlerMapping webSocketHandlerMapping() {
		ServletWebSocketHandlerRegistry registry = this.initHandlerRegistry();
		if (registry.requiresTaskScheduler()) {
			TaskScheduler scheduler = this.defaultSockJsTaskScheduler();
			Assert.notNull(scheduler, "Expected default TaskScheduler bean");
			registry.setTaskScheduler(scheduler);
		}
		
		return registry.getHandlerMapping();
	}
	
	private ServletWebSocketHandlerRegistry initHandlerRegistry() {
		if (this.handlerRegistry == null) {
			this.handlerRegistry = new ServletWebSocketHandlerRegistry();
			this.registerWebSocketHandlers(this.handlerRegistry);
		}
		
		return this.handlerRegistry;
	}
	
	protected void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	}
	
	@Primary
	@Bean
	@Nullable
	public TaskScheduler defaultSockJsTaskScheduler() {
		if (this.initHandlerRegistry().requiresTaskScheduler()) {
			ThreadPoolTaskScheduler threadPoolScheduler = new ThreadPoolTaskScheduler();
			threadPoolScheduler.setThreadNamePrefix("SockJS-");
			threadPoolScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
			threadPoolScheduler.setRemoveOnCancelPolicy(true);
			this.scheduler = threadPoolScheduler;
		}
		
		return this.scheduler;
	}
}
