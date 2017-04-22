package com.example.raghavkishan.sdsuhometownchat;

/**
 * Created by raghavkishan on 4/7/2017.
 */

public class Person {

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private String nickName;
    private String country;
    private String state;
    private String city;
    private String emailId;
    private int year;

    public Person(){}

    public Person(String nickName,String country,String state,String city,String emailId,int year){
        this.nickName = nickName;
        this.country = country;
        this.state = state;
        this.city = city;
        this.emailId = emailId;
        this.year = year;
    }

}
