import io.qameta.allure.gradle.AllureExtension

plugins {
    java
    maven
    id("io.qameta.allure") version "2.8.1"
}

group "io.qameta.allure.examples"
version 1.3

val seleniumVersion = "3.141.59"
val allureVersion = "2.13.6"
val junit5Version = "5.6.2"

tasks.withType(JavaCompile::class) {
    sourceCompatibility = "${JavaVersion.VERSION_1_8}"
    targetCompatibility = "${JavaVersion.VERSION_1_8}"
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}

configure<AllureExtension> {
    autoconfigure = true
    aspectjweaver = true
    version = allureVersion

    clean = true

    useJUnit5 {
        version = allureVersion
    }
}

tasks.withType(Test::class) {
    ignoreFailures = true
    useJUnitPlatform {

    }

    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.config.strategy", "dynamic")
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation("com.github.visual-regression-tracker:sdk-java:4.3.1")
    testImplementation("com.codeborne:selenide:5.14.0")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("io.qameta.allure:allure-java-commons:$allureVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
}
