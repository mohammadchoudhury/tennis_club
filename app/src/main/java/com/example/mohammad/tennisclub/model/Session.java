package com.example.mohammad.tennisclub.model;

/**
 * Created by mohammad on 09/03/18.
 */

public class Session {

    public enum Type {
        HEADER,
        SESSION
    }

    private Type type;
    private String date;
    private String time;

    public Session() {
    }

    public Session(String date) {
        this.date = date;
        this.type = Type.HEADER;
    }

    public Session(String date, String time) {
        this.date = date;
        this.time = time;
        this.type = Type.SESSION;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
