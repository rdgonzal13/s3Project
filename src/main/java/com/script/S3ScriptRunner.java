package com.script;

public class S3ScriptRunner {

    public static void main(String[] args) {
        final String bucket = "stellar-tht-candidates";
        final String key = "patients.log";

        S3FileParser parser = new S3FileParser();
        parser.readS3File(bucket, key);


    }
}


