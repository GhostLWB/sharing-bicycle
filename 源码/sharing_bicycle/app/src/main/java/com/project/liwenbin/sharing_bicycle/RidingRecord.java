package com.project.liwenbin.sharing_bicycle;

/**
 * Created by liwenbin on 2017/5/19 0019.
 */
public class RidingRecord {
    int ridetime;
    String date_time;
    public RidingRecord(int init_ride,String init_date){
        this.ridetime=init_ride;
        this.date_time=init_date;
    }

    public int getRidetime() {
        return ridetime;
    }

    public void setRidetime(int ridetime) {
        this.ridetime = ridetime;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
