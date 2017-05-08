package com.project.liwenbin.sharing_bicycle;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by liwenbin on 2017/4/29 0029.
 */
public class Bicycle {
    int bike_id;
    LatLng location;
    boolean in_use;
    boolean isBreakedown;
    String description;
    boolean isOrder;
    boolean isLock;
    public Bicycle(int init_bike_id,LatLng init_location,boolean init_in_use ,
                   boolean init_isBreakedown,String init_description,boolean init_isOrder,boolean init_isLock ){
        this.bike_id=init_bike_id;
        this.location=init_location;
        this.in_use=init_in_use;
        this.isBreakedown=init_isBreakedown;
        this.description=init_description;
        this.isOrder=init_isOrder;
        this.isLock=init_isLock;
    }

    /**
     * for test
     * @param init_location
     */
    public Bicycle(LatLng init_location){
        this.location=init_location;
    }

    public int getBike_id() {
        return bike_id;
    }

    public void setBike_id(int bike_id) {
        this.bike_id = bike_id;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean isIn_use() {
        return in_use;
    }

    public void setIn_use(boolean in_use) {
        this.in_use = in_use;
    }

    public boolean isBreakedown() {
        return isBreakedown;
    }

    public void setBreakedown(boolean breakedown) {
        isBreakedown = breakedown;
    }

    public boolean isOrder() {
        return isOrder;
    }

    public void setOrder(boolean order) {
        isOrder = order;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Bicycle{" +
                "bike_id=" + bike_id +
                ", location=" + location +
                ", in_use=" + in_use +
                ", isBreakedown=" + isBreakedown +
                ", description='" + description + '\'' +
                ", isOrder=" + isOrder +
                ", isLock=" + isLock +
                '}';
    }
}
