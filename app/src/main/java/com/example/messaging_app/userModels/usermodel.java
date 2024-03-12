package com.example.messaging_app.userModels;

public class usermodel {

    public String user_name;
    public String mobile_number;

    public usermodel(){

    }

    public usermodel(String user_name, String mobile_number){
        this.user_name = user_name;
        this.mobile_number = mobile_number;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
}
