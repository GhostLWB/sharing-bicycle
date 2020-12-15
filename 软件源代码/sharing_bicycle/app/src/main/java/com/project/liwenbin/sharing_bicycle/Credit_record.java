package com.project.liwenbin.sharing_bicycle;

/**
 * Created by liwenbin on 2017/5/24 0024.
 */
public class Credit_record {
    private String credit_amount;
    private String description;
    private String date_time;

    public Credit_record(String init_amount,String init_description,String init_date){
        this.credit_amount=init_amount;
        this.description=init_description;
        this.date_time=init_date;
    }

    public String getCredit_amount() {
        return credit_amount;
    }

    public void setCredit_amount(String
                                         credit_amount) {
        this.credit_amount = credit_amount;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
