val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    id("org.flywaydb.flyway") version "10.17.0"
}

group = "prof"
version = "0.0.2"
val ktor_version = "3.0.0-rc-2"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Exposed ORM + Hikari + SQLite JDBC
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")

    // Flyway migrations (core is enough for SQLite)
    implementation("org.flywaydb:flyway-core:10.17.0")

    // Date/time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("io.ktor:ktor-server-default-headers-jvm")

    //testImplementation("io.ktor:ktor-server-tests-jvm")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // bcrypt bibliotheek voor veilig wachtwoord hashen
    implementation("org.mindrot:jbcrypt:0.4")

    // Generates the OpenAPI spec from your routes:
    implementation("io.github.smiley4:ktor-openapi:5.3.0")

    // Serves Swagger UI for that spec:
    implementation("io.github.smiley4:ktor-swagger-ui:5.3.0")
    testImplementation("io.ktor:ktor-server-test-host-jvm:${ktor_version}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${kotlin_version}")


    implementation("org.junit.jupiter:junit-jupiter:5.10.0") // of de nieuwste versie
    tasks.test {
        useJUnitPlatform()
    }
}
