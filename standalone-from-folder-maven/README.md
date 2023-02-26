# Compare image and pdf files from a folder
This can be used to compare existing PNG and PDF files in a folder. Each page of PDF file is converted to ```BufferedImage``` and then compared in VRT.

#### Use it as a standalone executable jar file passing parameters from command line
- Make a fat jar by giving below maven command

    ```mvn clean compile assembly:single```
- Then run the jar file in command line as

    ```java -jar vrt-standalone-client-1.0.1.jar http://localhost:4200 CDJ3HHD5MY45G0PK5M3PBB6NBGC9 c936bfa4-50b3-40c4-b177-94efbeabfbcf null main <folder_name>```
- You can see help by running without parameters like below.

    ```java -jar vrt-standalone-client-1.0.1.jar```

#### You can also use hardcoded values for the VRT parameters in the code and run from IDE.
