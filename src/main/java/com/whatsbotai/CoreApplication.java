package com.whatsbotai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WhatsBotAI Core application entry point.
 *
 * <p>This Spring Boot application is the backend core of WhatsBotAI, a multi-tenant
 * SaaS that provides AI-powered WhatsApp customer service automation for cellphone
 * repair shops. It orchestrates conversation handling, LLM integration (Groq),
 * audio transcription (Whisper), web search diagnostics (Tavily), price approval
 * workflows, and real-time dashboard communication via WebSocket.
 *
 * <p>Architecture follows Clean Architecture with strict layer separation:
 * domain, application (use cases), infrastructure (adapters), and presentation
 * (controllers). Multi-tenancy is enforced at the database level through tenant
 * isolation filters.
 *
 * <p>Annotations enabled at startup:
 * <ul>
 *   <li>{@code @EnableCaching} — Caffeine-based caching for common responses</li>
 *   <li>{@code @EnableScheduling} — Background workers for message queue and billing</li>
 *   <li>{@code @EnableAsync} — Asynchronous task execution for non-blocking operations</li>
 * </ul>
 *
 * @author Kauan Santos Ferreira
 * @version 1.0
 * @since 2026
 */

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
public class CoreApplication {

	 /**
     * Application bootstrap method.
     *
     * @param args command-line arguments passed to the JVM
     */
	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}
