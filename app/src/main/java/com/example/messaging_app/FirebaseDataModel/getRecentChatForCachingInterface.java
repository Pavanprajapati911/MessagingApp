package com.example.messaging_app.FirebaseDataModel;

import com.google.firebase.database.DatabaseError;

public interface getRecentChatForCachingInterface {

    void onSuccess(chatMsgModel model, String nodeKey);
    void onFailure(DatabaseError error);
}
