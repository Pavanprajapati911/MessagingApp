package com.example.messaging_app.RoomDataModel;

import androidx.core.os.BuildCompat;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "All_Chats_Table")
public class All_Chats_Entity {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String chat_key;

    private String user_number;

    private String msg_by;

    private String chat_msg;


    @Ignore
    public All_Chats_Entity(String chat_key, String msg_by, String chat_msg, String user_number) {
        this.chat_key = chat_key;
        this.msg_by = msg_by;
        this.chat_msg = chat_msg;
        this.user_number = user_number;
    }

    public All_Chats_Entity() {
    }


    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }

    public String getMsg_by() {
        return msg_by;
    }

    public void setMsg_by(String msg_by) {
        this.msg_by = msg_by;
    }

    public String getChat_msg() {
        return chat_msg;
    }

    public void setChat_msg(String chat_msg) {
        this.chat_msg = chat_msg;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }
}
