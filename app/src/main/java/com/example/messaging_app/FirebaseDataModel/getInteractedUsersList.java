package com.example.messaging_app.FirebaseDataModel;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public interface getInteractedUsersList {

    void onSuccess(ArrayList<userDetailsModel> userList, ArrayList<String> nodeKeyList);

    void onFailure(DatabaseError error);

}
