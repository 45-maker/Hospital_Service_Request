package com.hospital.serviceapp.models;

public class Request {
    private int id;
    private int userId;
    private int serviceId;
    private String wardNumber;
    private String bedNumber;
    private String notes;
    private String status;
    private String timestamp;
    private String username;
    private String serviceName;

    public Request(int id, int userId, int serviceId, String wardNumber, String bedNumber,
                   String notes, String status, String timestamp, String username, String serviceName) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.wardNumber = wardNumber;
        this.bedNumber = bedNumber;
        this.notes = notes;
        this.status = status;
        this.timestamp = timestamp;
        this.username = username;
        this.serviceName = serviceName;
    }

    // Getters
    public int getId() { return id; }
    public String getWardNumber() { return wardNumber; }
    public String getBedNumber() { return bedNumber; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    public String getUsername() { return username; }
    public String getServiceName() { return serviceName; }
}