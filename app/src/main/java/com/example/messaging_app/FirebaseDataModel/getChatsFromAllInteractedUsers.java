package com.example.messaging_app.FirebaseDataModel;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public interface getChatsFromAllInteractedUsers {

    void onSuccess(ArrayList<ArrayList<chatMsgModel>> AllChatList);
    void onFailure(DatabaseError error);

}
