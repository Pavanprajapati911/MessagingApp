package com.example.messaging_app.FirebaseDataModel;


import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.messaging_app.userModels.usermodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirebaseModel {

    String USER_INTERACTIONS = "user_interactions";

    public static void FindPeopleinFirebase_RDB(String mobileNumber, getUserDetailsinterface userDetailsInterface) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(mobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    userDetailsInterface.onFailure(null);
                } else {
                    usermodel model = snapshot.getValue(usermodel.class);
                    userDetailsInterface.onSuccess(model, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getChatKey(DatabaseReference databaseReference, String mobileNumber, String otherUsersNumber, getRecentChatForCachingInterface chatForCachingInterface){

        databaseReference.child(mobileNumber+otherUsersNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String chatKey = mobileNumber+otherUsersNumber;
                    chatForCachingInterface.onSuccess(null, chatKey);
                }else {
                    String chatKey = otherUsersNumber+mobileNumber;
                    chatForCachingInterface.onSuccess(null, chatKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public Observable<ArrayList<userDetailsModel>> getAllInteractedUsers(DatabaseReference databaseReference, String mobileNumber){

        return Observable.create(new ObservableOnSubscribe<ArrayList<userDetailsModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<ArrayList<userDetailsModel>> emitter) throws Throwable {
                databaseReference.child(mobileNumber).child(USER_INTERACTIONS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<userDetailsModel> userList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            userList.add(dataSnapshot.getValue(userDetailsModel.class));

                        }
                        Log.d("TESTING", "onDataChange: "+userList.size());
                        emitter.onNext(userList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("MSG", "onCancelled: "+error);
                    }
                });
            }
        });
    }

    public Observable<String> addRecentInteractionToFirebaseDB(DatabaseReference databaseReference, String mobileNumber, userDetailsModel model){

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                databaseReference.child(mobileNumber).child(USER_INTERACTIONS).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        emitter.onNext("Data added bruh");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MSG", "onFailure: "+e);
                    }
                });
            }
        });
    }

    public Observable<String> addmultipleInteractionsToFirebaseDB(DatabaseReference databaseReference, String mobileNumber, ArrayList<userDetailsModel> interactedUsersList){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                for (userDetailsModel model : interactedUsersList){
                    databaseReference.child(mobileNumber).child(USER_INTERACTIONS).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            emitter.onNext("added bruh");
                        }
                    });
                }
            }
        });
    }



    public Observable<String> addChatToFirebaseDB(DatabaseReference databaseReference, String mobileNumber, String otherUsersNumber, chatMsgModelForFirebase model){

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                databaseReference.child(mobileNumber+otherUsersNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            databaseReference.child(mobileNumber+otherUsersNumber).child("all_chats").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    emitter.onNext("aahh bruh");

                                    chatMsgModel chatModel = new chatMsgModel();
                                    chatModel.setChat_key(mobileNumber+otherUsersNumber);
                                    chatModel.setChat_msg(model.getChat_msg());
                                    chatModel.setMsg_by(model.getMsg_by());
                                    chatModel.setUser_number(model.getUser_number());

                                    addChatForCachingToOtherUsersDevice(databaseReference, otherUsersNumber, chatModel).subscribeOn(Schedulers.io()).subscribe(new Observer<String>() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull String s) {
                                            Log.d("MSG", "onNext: "+s);
                                        }

                                        @Override
                                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                                }
                            });
                        }else {
                            databaseReference.child(otherUsersNumber+mobileNumber).child("all_chats").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    emitter.onNext("aahh bruh");

                                    chatMsgModel chatModel = new chatMsgModel();
                                    chatModel.setChat_key(otherUsersNumber+mobileNumber);
                                    chatModel.setChat_msg(model.getChat_msg());
                                    chatModel.setMsg_by(model.getMsg_by());
                                    chatModel.setUser_number(model.getUser_number());

                                    addChatForCachingToOtherUsersDevice(databaseReference, otherUsersNumber, chatModel).subscribeOn(Schedulers.io()).subscribe(new Observer<String>() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull String s) {
                                            Log.d("MSG", "onNext: "+s);
                                        }

                                        @Override
                                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("MSG", "onCancelled: "+error);
                    }
                });

            }
        });

    }

    public Observable<String> addChatForCachingToOtherUsersDevice(DatabaseReference databaseReference, String otherUserNumber, chatMsgModel model){

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                String childNode = "recent_chats_to_cached";

                databaseReference.child(otherUserNumber).child(childNode).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        emitter.onNext("haha");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emitter.onError(e);
                    }
                });
            }
        });
    }

    public Observable<ArrayList<chatMsgModel>> getAllChatForCaching(DatabaseReference databaseReference, String mobileNumber, getChatListToCachedInterface listToCachedInterface){

        return Observable.create(new ObservableOnSubscribe<ArrayList<chatMsgModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<ArrayList<chatMsgModel>> emitter) throws Throwable {
                String childNode = "recent_chats_to_cached";

                databaseReference.child(mobileNumber).child(childNode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<chatMsgModel> chatList = new ArrayList<>();
                        ArrayList<String> nodeKeyList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            chatList.add(dataSnapshot.getValue(chatMsgModel.class));
                            nodeKeyList.add(dataSnapshot.getKey());
                        }
                        emitter.onNext(chatList);
                        Log.d("OK", "onDataChange: "+chatList.size());
                        listToCachedInterface.onSuccess( nodeKeyList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("MSG", "onCancelled: "+error);
                    }
                });
            }
        });
    }

    public Observable<chatMsgModel> getRecentChatForCaching(DatabaseReference databaseReference, String mobileNumber, getRecentChatForCachingInterface chatForCachingInterface){

        return Observable.create(new ObservableOnSubscribe<chatMsgModel>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<chatMsgModel> emitter) throws Throwable {
                String childNode = "recent_chats_to_cached";

                databaseReference.child(mobileNumber).child(childNode).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String nodeKey = snapshot.getKey();
                        emitter.onNext(snapshot.getValue(chatMsgModel.class));
                        chatForCachingInterface.onSuccess(snapshot.getValue(chatMsgModel.class), nodeKey);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public Observable<ArrayList<ArrayList<chatMsgModel>>> getAllChatsWithInteractedUsers(DatabaseReference databaseReference, ArrayList<userDetailsModel> userList, String mobileNumber){

        return Observable.create(new ObservableOnSubscribe<ArrayList<ArrayList<chatMsgModel>>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<ArrayList<ArrayList<chatMsgModel>>> emitter) throws Throwable {
                ArrayList<ArrayList<chatMsgModel>> AllchatList = new ArrayList<>();
                for (userDetailsModel model : userList){

                    databaseReference.child(mobileNumber+model.getMobile_number()).child("all_chats").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                ArrayList<chatMsgModel> chatList = new ArrayList<>();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){


                                    String chat_Key =mobileNumber+model.getMobile_number();
                                    chatMsgModel chatmodel = new chatMsgModel();

                                    chatmodel.setChat_key(chat_Key);
                                    chatmodel.setChat_msg(dataSnapshot.child("chat_msg").getValue(String.class));
                                    chatmodel.setUser_number(dataSnapshot.child("user_number").getValue(String.class));
                                    chatmodel.setMsg_by(dataSnapshot.child("msg_by").getValue(String.class));
                                    Log.d("TESTING", "onDataChange: "+chatmodel.getChat_msg());
                                    chatList.add(chatmodel);
                                }
                                AllchatList.add(chatList);

                                if (AllchatList.size()==userList.size()){
                                    emitter.onNext(AllchatList);
                                }

                            }else {
                                databaseReference.child(model.getMobile_number()+mobileNumber).child("all_chats").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            ArrayList<chatMsgModel> chatList = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                                String chat_Key = model.getMobile_number()+mobileNumber;
                                                chatMsgModel chatmodel = new chatMsgModel();

                                                chatmodel.setChat_key(chat_Key);
                                                chatmodel.setChat_msg(dataSnapshot.child("chat_msg").getValue(String.class));
                                                chatmodel.setUser_number(dataSnapshot.child("user_number").getValue(String.class));
                                                chatmodel.setMsg_by(dataSnapshot.child("msg_by").getValue(String.class));
                                                Log.d("TESTING", "onDataChange: "+chatmodel.getChat_msg());
                                                chatList.add(chatmodel);

                                            }
                                            AllchatList.add(chatList);

                                            if (AllchatList.size()==userList.size()){
                                                emitter.onNext(AllchatList);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("MSG", "onCancelled: "+error);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("MSG", "onCancelled: "+error);
                        }
                    });

                }

            }
        });
    }

}