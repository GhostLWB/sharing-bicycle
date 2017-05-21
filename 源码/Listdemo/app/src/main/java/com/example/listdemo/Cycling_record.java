package com.example.listdemo;

/**
 * Created by 宋羽珩 on 2017/5/15.
 *
 *
 */
public class Cycling_record {
    private String bike_id;
    private String date_time;
    private int riding_time;

    public Cycling_record(String bike_id, String date_time, int riding_time) {
        this.bike_id = bike_id;
        this.date_time = date_time;
        this.riding_time = riding_time;
    }

    public String getBike_id() {
        return bike_id;
    }

    public void setBike_id(String bike_id) {
        this.bike_id = bike_id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getRiding_time() {
        return riding_time;
    }

    public void setRiding_time(int riding_time) {
        this.riding_time = riding_time;
    }
}
