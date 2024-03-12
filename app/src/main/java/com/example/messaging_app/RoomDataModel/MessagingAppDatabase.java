package com.example.messaging_app.RoomDataModel;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DataEntity.class, All_Chats_Entity.class},exportSchema = false, version = 1)
public abstract class MessagingAppDatabase extends RoomDatabase {

    public static MessagingAppDatabase dbInstance;

    public abstract DatabaseDao databaseDao();
    public abstract All_Chats_Dao allChatsDao();

    public static synchronized MessagingAppDatabase getDbInstance(Context context){

        if (dbInstance==null){

            dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                    MessagingAppDatabase.class,"MyAppDb")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return dbInstance;
    }



}
