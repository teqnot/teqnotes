plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    implementation("io.ktor:ktor-server-auth-jvm:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.7")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.google.code.gson:gson:2.8.9")

    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.7")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")

    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.2")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.2")

    testImplementation("com.github.docker-java:docker-java-api:3.4.0")
    testImplementation("com.github.docker-java:docker-java-transport-httpclient5:3.4.0")

    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.7")
    testImplementation("io.ktor:ktor-client-cio-jvm:2.3.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.7")
    testImplementation("com.h2database:h2:2.2.224")
}
