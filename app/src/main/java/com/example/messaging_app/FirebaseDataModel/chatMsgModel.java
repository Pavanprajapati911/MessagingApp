package com.example.messaging_app.FirebaseDataModel;

public class chatMsgModel {

    private String msg_by;

    private String chat_key;
    private String user_number;
    private String chat_msg;

    public chatMsgModel() {
    }


    public String getMsg_by() {
        return msg_by;
    }

    public void setMsg_by(String msg_by) {
        this.msg_by = msg_by;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getChat_msg() {
        return chat_msg;
    }

    public void setChat_msg(String chat_msg) {
        this.chat_msg = chat_msg;
    }

    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }
}
