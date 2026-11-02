# Visual Regression Tracker examples for Java

Each folder is a separate project on [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) 5.5.0 from JitPack (Java 17; JDI Light needs Java 21):

- [Playwright + Maven + JUnit 4](playwright)
- [Selenium 4 + Maven + JUnit 4](selenium-maven)
- [Selenide + Gradle + TestNG](selenide-gradle)
- [Allure + Selenide + Gradle (Kotlin DSL) + JUnit 5](allure-junit5-gradle-kts)
- [Standalone Java: compare PNG and PDF files from a folder + Maven](standalone-from-folder-maven)
- [JDI Light + TestNG + Maven](jdi-light-testng)
- [Appium (iOS) + TestNG + Maven](appium-maven)

The browser examples open https://the-internet.herokuapp.com headless. They connect to
`http://localhost:4200` with the default API key and project of a fresh
[Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker)
install; change that in each example's `VisualRegressionTrackerConfig`.

Run a Maven example with `mvn test`, a Gradle one with `./gradlew test`.
