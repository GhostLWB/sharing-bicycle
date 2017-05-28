package com.project.liwenbin.sharing_bicycle;

/**
 * Created by liwenbin on 2017/5/21 0021.
 */
public class PreorderHelp {
    int time_interval_help=-1;
    String preorder_bike_id;
    public static PreorderHelp help;
    private PreorderHelp(){
        this.time_interval_help=-1;
    }
    public static synchronized PreorderHelp getPreorderHelp(){
        if (help==null){
            help=new PreorderHelp();
        }
        return help;
    }

    public int getTime_interval_help() {
        return time_interval_help;
    }

    public void setTime_interval_help(int time_interval_help) {
        this.time_interval_help = time_interval_help;
    }

    public String getPreorder_bike_id() {
        return preorder_bike_id;
    }

    public void setPreorder_bike_id(String preorder_bike_id) {
        this.preorder_bike_id = preorder_bike_id;
    }
}
