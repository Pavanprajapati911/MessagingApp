package com.example.messaging_app.FirebaseDataModel;

import com.example.messaging_app.userModels.usermodel;
import com.google.firebase.database.DatabaseError;

public interface getUserDetailsinterface {

    void onSuccess(usermodel model, String nodeKey);
    void onFailure(DatabaseError error);

}
