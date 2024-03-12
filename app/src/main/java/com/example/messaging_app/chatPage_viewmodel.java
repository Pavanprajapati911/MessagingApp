package com.example.messaging_app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.messaging_app.FirebaseDataModel.chatMsgModel;
import com.example.messaging_app.FirebaseDataModel.chatMsgModelForFirebase;
import com.example.messaging_app.FirebaseDataModel.getRecentChatForCachingInterface;
import com.example.messaging_app.ModelRepository.Repository;
import com.example.messaging_app.RoomDataModel.All_Chats_Dao;
import com.example.messaging_app.RoomDataModel.All_Chats_Entity;
import com.example.messaging_app.RoomDataModel.DataEntity;
import com.example.messaging_app.RoomDataModel.DatabaseDao;
import com.example.messaging_app.RoomDataModel.MessagingAppDatabase;
import com.example.messaging_app.userModels.usermodel;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class chatPage_viewmodel extends AndroidViewModel {
    public Repository repository;
    public All_Chats_Dao allChatsDao;
    public DatabaseDao databaseDao;

    public chatPage_viewmodel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);
        allChatsDao = MessagingAppDatabase.dbInstance.allChatsDao();
        databaseDao = MessagingAppDatabase.dbInstance.databaseDao();

    }

    public LiveData<List<All_Chats_Entity>> getAllChatsWithInteractedUser(String chatKey){

        return allChatsDao.getChats(chatKey);
    }

    public void addChatsToRoomDB(String mobileNumber, String otherUsersNumber, chatMsgModelForFirebase chatMsgModelForFirebase){
        repository.addChatsToRoomDb(mobileNumber, otherUsersNumber, chatMsgModelForFirebase);
    }

    public void addChatsToFirebaseDB(String mobileNumber, String otherUsersNumber, chatMsgModelForFirebase chatMsgModelForFirebase){
        repository.addChatsToFirebaseDB(mobileNumber, otherUsersNumber, chatMsgModelForFirebase);
    }

    public Observable<DataEntity> getUser(String interactedUserNumber){

        return repository.getUser(interactedUserNumber);
    }

    public boolean checkUserLastInRoomDBListOrNot(String interactedUsersNumber){
        return repository.checkUserLastInRoomDBListOrNot(interactedUsersNumber);
    }

    public void replaceUserWithLastUserInRoomDB(String interactedUsersNumber){
        repository.replaceUserWithLastUserInRoomDB(interactedUsersNumber);
    }

    public void addRecentInteractionToRoomAndFirebase(String mobileNumber, usermodel interactedUserDetails){
        repository.addRecentInteractionToRoomDBAndFirebaseDB(mobileNumber, interactedUserDetails);
    }

    public void getChatKey( String mobileNumber, String otherUsersNumber, getRecentChatForCachingInterface chatForCachingInterface){
        repository.getChatKey(mobileNumber, otherUsersNumber, new getRecentChatForCachingInterface() {
            @Override
            public void onSuccess(chatMsgModel model, String nodeKey) {
                chatForCachingInterface.onSuccess(null, nodeKey);
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
    }

}
