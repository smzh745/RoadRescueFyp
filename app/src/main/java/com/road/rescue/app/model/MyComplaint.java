package com.road.rescue.app.model;

public class MyComplaint {
    private int id, uid;
    private String complaintType, city, complaintDetails, complaintImage, cDate, cTime;

    public MyComplaint(int id, int uid, String complaintType,
                       String city, String complaintDetails, String complaintImage, String cDate, String cTime) {
        this.id = id;
        this.uid = uid;
        this.complaintType = complaintType;
        this.city = city;
        this.complaintDetails = complaintDetails;
        this.complaintImage = complaintImage;
        this.cDate = cDate;
        this.cTime = cTime;
    }

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public String getCity() {
        return city;
    }

    public String getComplaintDetails() {
        return complaintDetails;
    }

    public String getComplaintImage() {
        return complaintImage;
    }

    public String getcDate() {
        return cDate;
    }

    public String getcTime() {
        return cTime;
    }
}
