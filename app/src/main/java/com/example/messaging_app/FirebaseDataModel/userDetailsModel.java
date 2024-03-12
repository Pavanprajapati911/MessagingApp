package com.example.messaging_app.FirebaseDataModel;

public class userDetailsModel {

    private String mobile_number;
    private String user_name;

    private String chat_key;


    public userDetailsModel() {
    }


    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }
}
