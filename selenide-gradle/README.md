# Selenide + Gradle + TestNG

[SelenideExample.java](src/test/java/SelenideExample.java) opens https://the-internet.herokuapp.com in headless Chrome with [Selenide](https://selenide.org) and tracks two screenshots with [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java), the second with test run options and an ignore area.

## Prerequisites

- JDK 17 or later; the Gradle wrapper downloads Gradle
- Google Chrome; Selenium Manager downloads a matching chromedriver
- A running [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) 6.0 server and its API key

## Setup

- Copy `vrt.json.example` to `vrt.json` and set your connection, or use the [`VRT_*` environment variables](https://github.com/Visual-Regression-Tracker/sdk-java#configuration), which override the file

## Run

```sh
./gradlew test
```

## What to expect

`enableSoftAssert` is on: the SDK logs a failed check through SLF4J instead of throwing, so the test passes. The examples have no SLF4J binding, so the log stays silent; add one, such as `slf4j-simple`, to see it.

- First run: the VRT UI shows a build with two new test runs, "Home page" and "Checkboxes page". Approve them to make them the baseline for the branch.
- Next runs: the test runs are `Ok` while the pages look the same. When one changes, it is `Unresolved`, and the build shows the diff to approve or reject.

Set `enableSoftAssert` to `false` to make new and changed screenshots fail the test.
