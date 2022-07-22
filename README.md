#S3 Log File Project


##Requirements
- Java 11


##How to run
- Set the following environment variables with AWS credentials 
````$xslt
export AWS_ACCESS_KEY_ID="access-key-value"
export AWS_SECRET_ACCESS_KEY="secret-key-value"
````
- Navigate to project directory 
- Run ./gradlew build to build project
- Run ./gradlew run to run script
- New patient file will be located in project directory