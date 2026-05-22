/*
 * WhatsBotAI Core - Build Configuration
 *
 * Gradle build script using Kotlin DSL (type-safe, IDE-friendly).
 * All dependencies are version-managed by Spring Boot Dependency Management plugin.
 *
 * @author Kauan Santos Ferreira
 * @version 1.0
 * @since 2026
 */

/*
Plugins que ensinam ao Grodle a realizar tarefas
@param java - Ensina o Grodle a copilar .java
@param org.springframework.boo - Plugins do Spring
@param io.spring.dependency-management - Plugins que gerencia versões de dependência do Spring
*/
plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}


group = "com.whatsbotai"
version = "0.0.1-SNAPSHOT"
description = "WhatsBotAI Core - WhatsApp AI Bot SaaS Backend"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

// Custom configuration to exclude default logging in favor of our setup later
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

// ─────────────────────────────────────────────────────────────────────────────
// Dependency versions (centralized for easy upgrades)
// ─────────────────────────────────────────────────────────────────────────────
val jjwtVersion = "0.12.6"
val springdocVersion = "2.8.6"
val mapstructVersion = "1.6.3"
val resilience4jVersion = "2.3.0"
val bucket4jVersion = "8.10.1"
val caffeineVersion = "3.1.8"
val pgvectorVersion = "0.1.6"

dependencies {
	// ─── Core Spring Boot Starters ───────────────────────────────────────────
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	// ─── Database ────────────────────────────────────────────────────────────
	implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")
	// pgvector for RAG embeddings (vector similarity search)
	implementation("com.pgvector:pgvector:$pgvectorVersion")

	// ─── Authentication & Security (JWT) ─────────────────────────────────────
	implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

	// ─── API Documentation (Swagger / OpenAPI) ───────────────────────────────
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

	// ─── DTO Mapping (MapStruct) ─────────────────────────────────────────────
	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

	// ─── Resilience (Circuit Breaker, Retry, Rate Limiter) ───────────────────
	implementation("io.github.resilience4j:resilience4j-spring-boot3:$resilience4jVersion")
	implementation("io.github.resilience4j:resilience4j-reactor:$resilience4jVersion")

	// ─── Rate Limiting (Bucket4j) ────────────────────────────────────────────
	implementation("com.bucket4j:bucket4j-core:$bucket4jVersion")

	// ─── Caching (Caffeine in-memory cache) ──────────────────────────────────
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")

	// ─── Lombok (boilerplate reduction) ──────────────────────────────────────
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// ─── Development tools (hot reload) ──────────────────────────────────────
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// ─── Testing ─────────────────────────────────────────────────────────────
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ─────────────────────────────────────────────────────────────────────────────
// Test configuration
// ─────────────────────────────────────────────────────────────────────────────
tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
		events("passed", "skipped", "failed")
		showStandardStreams = false
	}
}

// ─────────────────────────────────────────────────────────────────────────────
// Compiler configuration
// ─────────────────────────────────────────────────────────────────────────────
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.compilerArgs.add("-parameters") // Required for Spring parameter name resolution
}

// ─────────────────────────────────────────────────────────────────────────────
// Bootable JAR configuration
// ─────────────────────────────────────────────────────────────────────────────
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("whatsbotai-core.jar")
}

