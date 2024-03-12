package com.example.messaging_app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.messaging_app.ModelRepository.Repository;
import com.example.messaging_app.RoomDataModel.All_Chats_Entity;
import com.example.messaging_app.RoomDataModel.DataEntity;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    public LiveData<List<DataEntity>> getAllInteractionsList;

    Repository repository;
    public LiveData<List<DataEntity>> allInteractionList;
    public LiveData<List<All_Chats_Entity>> chatList;


    public ViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);
        getAllInteractionsList = repository.getAllInteractions();

    }

    public LiveData<List<DataEntity>> getInteractionList(){
        return getAllInteractionsList;
    }

    public void getAllInteractionsAndTheirChats(String mobileNumber){
        repository.getAllInteractionsAndTheirChats(mobileNumber);
    }

    public void addAllchatsForCachingInRoomDB(String mobileNumber, boolean openFirstTimeAfterInstall){
        repository.addAllChatsForCachingInRoomDB(mobileNumber, openFirstTimeAfterInstall);
    }

    public void addRecentChatInRoomDB(String mobileNumber){
        repository.addRecentChatInRoomDBForCaching(mobileNumber);
    }



}
