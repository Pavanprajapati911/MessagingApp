package com.example.messaging_app.FirebaseDataModel;

public class chatMsgModelForFirebase {

    private String msg_by;

    private String user_number;
    private String chat_msg;

    public chatMsgModelForFirebase() {
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
}
