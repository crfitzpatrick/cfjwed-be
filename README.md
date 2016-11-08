# cfjwed-be
This is the backend source code to my wedding's invitation application.

Please note that this is still a WIP and is still being optimized and tested.

#### How to Run
1. Copy src/main/resources/application.properties to /usr/local/cfjwed/application.properties
2. Adjust the configuration as needed
3. In your RDBMS, create the database configured in the step above
4. Build the source by running `mvn package`
5. Run the jar in the target directory by executing `java -jar target/app-1.0-SNAPSHOT.jar`
