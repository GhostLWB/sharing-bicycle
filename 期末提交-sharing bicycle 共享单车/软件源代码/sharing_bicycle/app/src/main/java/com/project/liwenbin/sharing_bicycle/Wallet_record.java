package com.project.liwenbin.sharing_bicycle;

/**
 * Created by 宋羽珩 on 2017/5/15.
 *
 *
 */
public class Wallet_record {
    private String amount;
    private String date_time;

    public Wallet_record(String initAmount, String date_time) {
        this.amount = initAmount;
        this.date_time = date_time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
