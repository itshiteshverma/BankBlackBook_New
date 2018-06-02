package com.itshiteshverma.bankblackbook.HelperClass;

public class MyObject {

    public String objectName, objectValue;

    // constructor for adding sample data
    public MyObject(String objectName, String objectValue) {

        this.objectName = objectName;
        this.objectValue = objectValue;
    }


    public MyObject(String objectValue) {
        this.objectValue = objectValue;
    }
}