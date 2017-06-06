package com.project.liwenbin.sharing_bicycle;

/**
 * Created by liwenbin on 2017/5/3 0003.
 */
public class User  {
    String account=null;
    String haveBicycleID=null;
    String sex;
    double balance;
    double credit;

    private static User user;
    private User(){
    }
    public static synchronized User getUser(){
        if (user==null){
            user=new User();
        }
        return user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHaveBicycleID() {
        return haveBicycleID;
    }

    public void setHaveBicycleID(String haveBicycleID) {
        this.haveBicycleID = haveBicycleID;
    }
}
