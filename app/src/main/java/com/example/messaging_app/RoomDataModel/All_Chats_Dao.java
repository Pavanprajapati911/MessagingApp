package com.example.messaging_app.RoomDataModel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface All_Chats_Dao {

    @Insert
    void insert(All_Chats_Entity entity);

    @Query("SELECT * FROM All_Chats_Table WHERE chat_key = :chatKey")
    LiveData<List<All_Chats_Entity>> getChats(String chatKey);


}
