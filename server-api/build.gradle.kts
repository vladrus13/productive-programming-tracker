val ktorVersion: String by project
val exposedVersion: String by project
val kotlinProcessVersion: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    application
}

group = "ru.productive"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.19.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2:2.1.214")
    implementation("com.github.pgreze:kotlin-process:$kotlinProcessVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}