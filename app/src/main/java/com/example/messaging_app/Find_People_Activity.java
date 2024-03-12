package com.example.messaging_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messaging_app.Adapters.Find_People_Activity_RvAdapter;
import com.example.messaging_app.FirebaseDataModel.getUserDetailsinterface;
import com.example.messaging_app.FirebaseDataModel.userDetailsModel;
import com.example.messaging_app.userModels.usermodel;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.observers.TestObserver;

public class Find_People_Activity extends AppCompatActivity {

    EditText searchBar;
    RecyclerView recyclerView;

    ArrayList<usermodel> arrayLis;
    usermodel userModel;
    String mobileNumber;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);
        recyclerView = findViewById(R.id.find_people_rv);
        searchBar = findViewById(R.id.searchbar);
        backButton = findViewById(R.id.find_people_backbutton);

        arrayLis = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("userdetails",MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("mobilenumber","no value");

        find_People_Activity_ViewModel viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(find_People_Activity_ViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Find_People_Activity_RvAdapter adapter = new Find_People_Activity_RvAdapter(arrayLis, new Find_People_Activity_RvAdapter.setOnCLick() {
            @Override
            public void onClick() {
                Intent intent = new Intent(Find_People_Activity.this, ChatPage_Activity.class);
                intent.putExtra("username",userModel.getUser_name());
                intent.putExtra("usernumber",userModel.getMobile_number());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH){

                    viewModel.findPeople("+91" + searchBar.getText().toString(), new getUserDetailsinterface() {
                        @Override
                        public void onSuccess(usermodel model, String nodeKey) {
                                if (Objects.equals(model.getMobile_number(), mobileNumber)){
                                    Toast.makeText(Find_People_Activity.this,"Cant search your number enter different one", Toast.LENGTH_LONG).show();
                                }else {
                                    arrayLis.add(model);
                                    userModel = model;
                                    adapter.notifyDataSetChanged();
                                }
                        }

                        @Override
                        public void onFailure(DatabaseError error) {
                            Toast.makeText(Find_People_Activity.this, "user not Found",Toast.LENGTH_LONG).show();
                        }
                    });

                }

                return true;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }



}