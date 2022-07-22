package com.script;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class S3FileParser {

    final AmazonS3 s3;

    public S3FileParser() {

        String accessKeyEnv = "AWS_ACCESS_KEY_ID";
        String accessKey = System.getenv(accessKeyEnv);
        String secretKeyEnv = "AWS_SECRET_ACCESS_KEY";
        String secretKey = System.getenv(secretKeyEnv);

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(Regions.DEFAULT_REGION).build();
    }

    public void readS3File(String bucket, String key) {
        var fileName = "patients.log";
        var resp = s3.getObject(bucket, key);
        try (S3ObjectInputStream inStream = resp.getObjectContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./" + fileName)))) {
            String l = reader.readLine();
            while (l != null) {
                writer.write(parseLine(l));
                writer.newLine();
                l = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String parseLine(String logEntry) {
        final String DOB = "DOB='";
        final String DATE_OF_BIRTH = "DATE_OF_BIRTH='";
        if (!logEntry.contains(DOB) && !logEntry.contains(DATE_OF_BIRTH)) {
            return logEntry;
        } else {
            int startDate;
            int startField;
            if (logEntry.contains(DOB)) {
                startField = logEntry.indexOf(DOB);
                startDate = startField + DOB.length();
            } else {
                startField = logEntry.indexOf(DATE_OF_BIRTH);
                startDate = startField + DATE_OF_BIRTH.length();
            }
            int end = logEntry.indexOf("'", startDate + 1);
            StringBuilder dataBuilder = new StringBuilder();
            String rawDateField = logEntry.substring(startDate, end);
            if (rawDateField.equals("***")) {
                dataBuilder.append(logEntry, 0, startField);
                //remove trailing single quote and space from field
                dataBuilder.append(logEntry.substring(end + 2));
            } else {
                String sanitizedData = scrubDOB(rawDateField);
                dataBuilder.append(logEntry, 0, startDate);
                dataBuilder.append(sanitizedData);
                dataBuilder.append(logEntry.substring(end));
            }
            return dataBuilder.toString();
        }
    }


    private String scrubDOB(String birthDate) {
        birthDate = birthDate.trim();

        if (birthDate.length() >= 4) {
            String year = birthDate.substring(birthDate.length() - 4, birthDate.length());
            return "X/X/" + year;
        }
        return birthDate;
    }

}
