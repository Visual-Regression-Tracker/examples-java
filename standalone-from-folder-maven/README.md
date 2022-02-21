# 3 ways to use this example

### Use it as a standalone executable jar file with option to  pass parameters from command line
- Ensure in the main method, you are using the only line ```option1_RunWithParameters```
- Make a fat jar by giving below maven command

    ```mvn clean compile assembly:single```
- Then you can use run the jar file in command line as

    ```java -jar vrt-standalone-client-1.0.0.jar http://localhost:4200 CDJ3HHD5MY45G0PK5M3PBB6NBGC9 c936bfa4-50b3-40c4-b177-94efbeabfbcf null main```
- You can go through help

    ```java -jar vrt-standalone-client-1.0.0.jar```
### Use it as a standalone executable jar file with hardcoded options
- Ensure in the main method, you are using the only line ```option2_RunWithHardcodedValue```
- Ensure values are correct in ```option2_RunWithHardcodedValue```
- Run from command line ```java -jar vrt-standalone-client-1.0.0.jar``` 

### Use it from the code
- Ensure in the main method, you are using the only line ```option2_RunWithHardcodedValue```
- Ensure values are correct in ```option2_RunWithHardcodedValue```
- Run from IDE 
