# Appium (iOS) + Maven + TestNG

[IOSBasicInteractionsTest.java](src/test/java/IOSBasicInteractionsTest.java) opens six screens of the sample app in [App.zip](src/main/resources/App.zip) on an iOS simulator with the [Appium Java client](https://github.com/appium/java-client) and tracks each with [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java). A screenshot that comes back `Unresolved` is taken again up to two times. [BaseTest.java](src/test/java/BaseTest.java) starts a local Appium server for the suite.

CI only compiles this example: it needs macOS with an iOS simulator.

## Prerequisites

- macOS with Xcode and the simulator named in the test (`iPhone 12`, iOS 14.5); change `deviceName` and `platformVersion` to one you have
- [Appium](https://appium.io/docs/en/latest/quickstart/) with the XCUITest driver: `npm install -g appium`, then `appium driver install xcuitest`
- JDK 17 or later and Maven
- A running [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) 6.0 server and its API key

## Setup

- Copy `vrt.json.example` to `vrt.json` and set your connection, or use the [`VRT_*` environment variables](https://github.com/Visual-Regression-Tracker/sdk-java#configuration), which override the file

## Run

```sh
mvn test
```

## What to expect

`enableSoftAssert` is on: the SDK logs a failed check through SLF4J instead of throwing, so the test passes. The examples have no SLF4J binding, so the log stays silent; add one, such as `slf4j-simple`, to see it.

- First run: the VRT UI shows a build with six new test runs, one per screen, from "Home page" to "Drag page". Approve them to make them the baseline for the branch.
- Next runs: the test runs are `Ok` while the screens look the same. When one changes, it is `Unresolved`, and the build shows the diff to approve or reject.

Set `enableSoftAssert` to `false` to make new and changed screenshots fail the test.
