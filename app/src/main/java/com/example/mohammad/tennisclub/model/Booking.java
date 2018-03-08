package com.example.mohammad.tennisclub.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Booking {

    private enum Court {
        ONE,
        TWO,
        THREE,
        FOUR;
    }

    private enum Type {
        NORMAL,
        GROUP,
        PRIVATE;
    }

    private Court court;
    private Type type;
    private Date dateTime;

//    SimpleDateFormat sdf = new SimpleDateFormat("dd mmm yyyy hh:mm");
//    Date date1 = sdf.parse("2009-12-31");

    public Booking() throws ParseException {}

    public Booking(Court court, Type type, Date dateTime) throws ParseException {
        this.court = court;
        this.type = type;
        this.dateTime = dateTime;
    }

    public Court getCourt() { return court; }

    public void setCourt(Court court) { this.court = court; }

    public Type getType() { return type; }

    public void setType(Type type) { this.type = type; }

    public Date getDateTime() { return dateTime; }

    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }

}
