# Compare PNG and PDF files from a folder

[CompareFromFile.java](src/main/java/CompareFromFile.java) is a command-line client: it tracks every PNG file in a folder, and every page of every PDF file as a PNG, with [sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java). Use it to compare images that another tool produced.

CI only compiles this example: it needs a folder of images.

## Prerequisites

- JDK 25 or later and Maven
- A running [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) 6.0 server and its API key

## Setup

- Copy `vrt.json.example` to `vrt.json` in the folder you run the client from and set your connection, or use the [`VRT_*` environment variables](https://github.com/Visual-Regression-Tracker/sdk-java#configuration), which override the file
- Build a jar with its dependencies: `mvn compile assembly:single`

## Run

```sh
java -jar target/vrt-standalone-client-1.0.1.jar <folder>
```

Without `<folder>`, the client reads the current folder; `help` prints the usage. You can also run `CompareFromFile` from your IDE.

## What to expect

The client prints the build status and a summary: how many files it processed, how many runs of each status, and whether all passed. It also writes the counts as a chart to `vrt_result.html` in the current folder.

- First run: every image is `New`. The VRT UI shows a build with one test run per PNG file and per PDF page, named after the file (`report_Page1.png` for the first page of `report.pdf`). Approve them to make them the baseline for the branch.
- Next runs: `Ok` while the images are the same. When one changes, it is `Unresolved`, and the build shows the diff to approve or reject.

`enableSoftAssert` only decides whether the SDK logs or throws; the client handles both, and counts new and changed images as not passed.
