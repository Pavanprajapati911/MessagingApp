package com.example.messaging_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messaging_app.Adapters.ChatPageActivity_Adapter;
import com.example.messaging_app.FirebaseDataModel.chatMsgModel;
import com.example.messaging_app.FirebaseDataModel.chatMsgModelForFirebase;
import com.example.messaging_app.FirebaseDataModel.getRecentChatForCachingInterface;
import com.example.messaging_app.RoomDataModel.All_Chats_Entity;
import com.example.messaging_app.RoomDataModel.DataEntity;
import com.example.messaging_app.userModels.usermodel;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatPage_Activity extends AppCompatActivity {

    String chatKey;
    String mobileNumber, username;
    TextView userNameToShow;

    EditText textMsg;
    ImageView sendMsg, backWordButton;

    RecyclerView recyclerView;
    ChatPageActivity_Adapter adapter;

    Observable<Boolean> msgSendObservable;

    boolean firstMsgToOtherUser;
   io.reactivex.rxjava3.core.Observer<Boolean> msgSendObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        userNameToShow = findViewById(R.id.chatpage_username);
        textMsg = findViewById(R.id.textmessage);
        sendMsg = findViewById(R.id.sendmsg);
        backWordButton = findViewById(R.id.backwordbutton);
        recyclerView = findViewById(R.id.chatsRv);

        firstMsgToOtherUser = true;
        SharedPreferences sharedPreferences = getSharedPreferences("userdetails",MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("mobilenumber","no value");
        username = sharedPreferences.getString("username","no value");

        String otherUserName = getIntent().getStringExtra("username");
        String otherUserNumber = getIntent().getStringExtra("usernumber");
        userNameToShow.setText(otherUserName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatPageActivity_Adapter(mobileNumber);
        recyclerView.setAdapter(adapter);

        chatPage_viewmodel viewmodel = new ViewModelProvider(this).get(chatPage_viewmodel.class);

        viewmodel.getUser(otherUserNumber).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.rxjava3.core.Observer<DataEntity>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull DataEntity entity) {

                if (entity==null){
                    Log.d("TESTING", "onNext: no chat found");
                }else {
                    chatKey = entity.getChat_key();

                    firstMsgToOtherUser = false;
                    viewmodel.getAllChatsWithInteractedUser(chatKey).observe(ChatPage_Activity.this, new Observer<List<All_Chats_Entity>>() {
                        @Override
                        public void onChanged(List<All_Chats_Entity> all_chats_entities) {
                            Log.d("TESTING", "onChanged: "+chatKey);
                            adapter.setChatList(all_chats_entities);
                        }
                    });

                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        backWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textMsg!=null){

                    chatMsgModelForFirebase chatModel = new chatMsgModelForFirebase();
                    chatModel.setChat_msg(textMsg.getText().toString());
                    chatModel.setUser_number(mobileNumber);
                    chatModel.setMsg_by(username);

                    usermodel model = new usermodel();
                    model.setUser_name(otherUserName);
                    model.setMobile_number(otherUserNumber);

                    viewmodel.addRecentInteractionToRoomAndFirebase(mobileNumber, model);
                    viewmodel.addChatsToRoomDB(mobileNumber, otherUserNumber, chatModel);
                    viewmodel.addChatsToFirebaseDB(mobileNumber, otherUserNumber, chatModel);

                    if (firstMsgToOtherUser){

                        viewmodel.getChatKey(mobileNumber, otherUserNumber, new getRecentChatForCachingInterface() {
                            @Override
                            public void onSuccess(chatMsgModel model, String nodeKey) {
                                viewmodel.getAllChatsWithInteractedUser(nodeKey).observe(ChatPage_Activity.this, new Observer<List<All_Chats_Entity>>() {
                                    @Override
                                    public void onChanged(List<All_Chats_Entity> all_chats_entities) {
                                        Log.d("TESTING", "onChanged: "+chatKey);
                                        adapter.setChatList(all_chats_entities);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(DatabaseError error) {

                            }
                        });
                    }

                    msgSendObservable = Observable.create(new ObservableOnSubscribe<Boolean>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                            if (!viewmodel.checkUserLastInRoomDBListOrNot(otherUserNumber)){
                                Log.d("MSGSEND", "subscribe: "+otherUserNumber);
                                viewmodel.replaceUserWithLastUserInRoomDB(otherUserNumber);
                                emitter.onNext(true);
                            }
                        }
                    });

                    msgSendObserver = new io.reactivex.rxjava3.core.Observer<Boolean>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (aBoolean){
                                Log.d("MSGSEND", "onNext: user updated in room");
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    };
                    msgSendObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(msgSendObserver);


                    textMsg.getText().clear();


                }else {
                    Toast.makeText(ChatPage_Activity.this, "Text is empty", Toast.LENGTH_LONG).show();
                }
            }
        });



    }
}