package com.example.mohammad.tennisclub.model;

import com.google.firebase.firestore.DocumentReference;

public class Booking extends Session {

    private String location;
    private DocumentReference user;
    public String coach;

    public Booking() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }
}
