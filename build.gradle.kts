import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "no.nav.helse.flex"
version = "1.0"
description = "flex-juridisk-vurdering-test-backend"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

val logstashLogbackEncoderVersion = "7.4"
val testContainersVersion = "1.19.7"
val kluentVersion = "1.73"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")

    testImplementation(platform("org.testcontainers:testcontainers-bom:$testContainersVersion"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
        if (System.getenv("CI") == "true") {
            allWarningsAsErrors.set(true)
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading")
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
            exceptionFormat = TestExceptionFormat.FULL
        }
        failFast = false
    }
}

tasks {
    bootJar {
        archiveFileName = "app.jar"
    }
}
