plugins {
    java
    id("io.qameta.allure") version "4.3.0"
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}

dependencies {
    testImplementation("com.github.Visual-Regression-Tracker:sdk-java:5.5.0")
    testImplementation("com.codeborne:selenide:7.19.1")
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    ignoreFailures = true
}
