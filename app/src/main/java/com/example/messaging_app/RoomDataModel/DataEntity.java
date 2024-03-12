package com.example.messaging_app.RoomDataModel;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "interactions_table")
public class DataEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String interacted_users_number;
    private String interacted_users_name;



    private String chat_key;

    public DataEntity(){

    }

    @Ignore
    public DataEntity(String interacted_users_number, String interacted_users_name, String chat_key){
        this.interacted_users_number = interacted_users_number;
        this.interacted_users_name = interacted_users_name;
        this.chat_key = chat_key;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInteracted_users_number() {
        return interacted_users_number;
    }

    public void setInteracted_users_number(String interacted_users_number) {
        this.interacted_users_number = interacted_users_number;
    }

    public String getInteracted_users_name() {
        return interacted_users_name;
    }

    public void setInteracted_users_name(String interacted_users_name) {
        this.interacted_users_name = interacted_users_name;
    }

    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }
}
