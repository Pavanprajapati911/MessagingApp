package com.example.messaging_app;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.room.Ignore;

import com.example.messaging_app.FirebaseDataModel.getUserDetailsinterface;
import com.example.messaging_app.FirebaseDataModel.userDetailsModel;
import com.example.messaging_app.ModelRepository.Repository;
import com.example.messaging_app.userModels.usermodel;
import com.google.firebase.database.DatabaseError;


public class find_People_Activity_ViewModel extends AndroidViewModel {

    public Repository repository;

    public find_People_Activity_ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }


    public void findPeople(String mobileNumber, getUserDetailsinterface userDetailsInterface){
        repository.findPeopleInFirebaseDB(mobileNumber, new getUserDetailsinterface() {
            @Override
            public void onSuccess(usermodel model, String nodeKey) {
                userDetailsInterface.onSuccess(model, null);
            }

            @Override
            public void onFailure(DatabaseError error) {
                userDetailsInterface.onFailure(error);
            }
        });
    }

}
