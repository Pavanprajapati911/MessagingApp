package com.example.messaging_app.FirebaseDataModel;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public interface getChatListToCachedInterface {

    void onSuccess( ArrayList<String> nodeKeyList);
    void onFailure(DatabaseError error);

}
