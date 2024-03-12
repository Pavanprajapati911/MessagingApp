package com.example.messaging_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.messaging_app.Adapters.Find_People_Activity_RvAdapter;
import com.example.messaging_app.Adapters.mainActivityAdapter;
import com.example.messaging_app.RoomDataModel.DataEntity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView findPeople;
    RecyclerView chatListRv;
    String mobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findPeople = findViewById(R.id.find_peope_icon);
        chatListRv = findViewById(R.id.chatListRv);

        SharedPreferences sharedPreferences = getSharedPreferences("userdetails",MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("mobilenumber","no value");

        findPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Find_People_Activity.class);
                startActivity(intent);
            }
        });

        chatListRv.setLayoutManager(new LinearLayoutManager(this));
        mainActivityAdapter adapter = new mainActivityAdapter(new mainActivityAdapter.OnClick() {
            @Override
            public void onClick(String otherUserName, String otherUserNumber) {
                Intent intent = new Intent(MainActivity.this, ChatPage_Activity.class);
                intent.putExtra("username", otherUserName);
                intent.putExtra("usernumber", otherUserNumber);
                startActivity(intent);
            }
        });

        chatListRv.setAdapter(adapter);

        SharedPreferences installPreference = getSharedPreferences("FirstTimeAfterInstall",MODE_PRIVATE);
        boolean FirstTimeOpenAfterInstall = installPreference.getBoolean("FirstTimeOpenAfterInstall",false);

        ViewModel viewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ViewModel.class);

        if (!FirstTimeOpenAfterInstall){

            viewModel.addAllchatsForCachingInRoomDB(mobileNumber, false);
            viewModel.getInteractionList().observe(this, new Observer<List<DataEntity>>() {
                @Override
                public void onChanged(List<DataEntity> dataEntities) {
                    adapter.setUserlist(dataEntities);
                }
            });

        }else {

            SharedPreferences.Editor editor = installPreference.edit();
            editor.putBoolean("FirstTimeOpenAfterInstall",false);
            editor.apply();

            viewModel.getAllInteractionsAndTheirChats(mobileNumber);
            viewModel.getInteractionList().observe(this, new Observer<List<DataEntity>>() {
                @Override
                public void onChanged(List<DataEntity> dataEntities) {
                    adapter.setUserlist(dataEntities);

                }
            });

            viewModel.addAllchatsForCachingInRoomDB(mobileNumber, true);
        }


    }
}