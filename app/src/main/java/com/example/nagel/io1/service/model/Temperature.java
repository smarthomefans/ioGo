package com.example.nagel.io1.service.model;

public class Temperature {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Temperature(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
