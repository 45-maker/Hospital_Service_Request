package com.hospital.serviceapp.models;

public class Service {
    private int id;
    private String code;
    private String name;

    public Service(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    // Getters
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
}