# Compare image and pdf files from a folder
This can be used to compare existing png images in a folder. If there are pdf files in the folder, they will be converted to png files and then will be compared in VRT.

#### Use it as a standalone executable jar file with option to  pass parameters from command line
- Ensure in the main method, you are using the only line ```option1_RunWithParameters```
- Make a fat jar by giving below maven command

    ```mvn clean compile assembly:single```
- Then you can use run the jar file in command line as

    ```java -jar vrt-standalone-client-1.0.1.jar http://localhost:4200 CDJ3HHD5MY45G0PK5M3PBB6NBGC9 c936bfa4-50b3-40c4-b177-94efbeabfbcf null main```
- You can go through help

    ```java -jar vrt-standalone-client-1.0.1.jar```

#### You can also use hardcoded values for the VRT parameters in the code and run from IDE.
