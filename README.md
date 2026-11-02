# Visual Regression Tracker examples for Java

Each folder is a separate project with its own build, `vrt.json.example` and README, on [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) 5.5.0 from JitPack and Java 17 or later. The browser examples open https://the-internet.herokuapp.com headless and track screenshots in a [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) 6.0 server.

| Example                                                      | Stack                                                | Client                                                            |
| ------------------------------------------------------------ | ---------------------------------------------------- | ----------------------------------------------------------------- |
| [playwright](playwright)                                     | Playwright, Maven, JUnit 4                           | [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) |
| [selenium-maven](selenium-maven)                             | Selenium 4, Maven, JUnit 4                           | [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) |
| [selenide-gradle](selenide-gradle)                           | Selenide, Gradle, TestNG                             | [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) |
| [appium-maven](appium-maven)                                 | Appium on an iOS simulator, Maven, TestNG            | [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) |
| [standalone-from-folder-maven](standalone-from-folder-maven) | Command line: PNG and PDF files from a folder, Maven | [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java) |

The examples connect with `vrt.json` or `VRT_*` environment variables, the way [sdk-java reads its configuration](https://github.com/Visual-Regression-Tracker/sdk-java#configuration). `vrt.json.example` holds the URL, API key and project of a fresh local install; copy it to `vrt.json` and change what differs.

CI runs the browser examples against a VRT server started from [.github/vrt/docker-compose.yml](.github/vrt/docker-compose.yml) and compiles the other two.
