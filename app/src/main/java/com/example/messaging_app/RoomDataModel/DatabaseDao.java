package com.example.messaging_app.RoomDataModel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DatabaseDao {


    @Insert
    void insert(DataEntity entity);

    @Query("Select * from interactions_table WHERE interacted_users_number = :interactedUsersNumber")
    DataEntity getUser(String interactedUsersNumber);


    @Query("Select * from interactions_table order by id DESC limit 1")
    DataEntity getLastUserInRoomDB();

    @Update
    void update(DataEntity entity);

    @Delete
    void delete(DataEntity entity);

    @Query("Select * from interactions_table order by id DESC")
    LiveData<List<DataEntity>> getInteractionList();



}
