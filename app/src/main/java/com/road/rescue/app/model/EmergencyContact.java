package com.road.rescue.app.model;

public class EmergencyContact {
    private int uid, Id;
    private String eName, eContact;

    public EmergencyContact(String eName, String eContact) {
        this.eName = eName;
        this.eContact = eContact;
    }

    public EmergencyContact(int uid, int id, String eName, String eContact) {
        this.uid = uid;
        Id = id;
        this.eName = eName;
        this.eContact = eContact;
    }

    public int getUid() {
        return uid;
    }

    public int getId() {
        return Id;
    }

    public String geteName() {
        return eName;
    }

    public String geteContact() {
        return eContact;
    }
}
