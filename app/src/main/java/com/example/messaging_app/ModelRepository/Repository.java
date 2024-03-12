package com.example.messaging_app.ModelRepository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.messaging_app.FirebaseDataModel.FirebaseModel;
import com.example.messaging_app.FirebaseDataModel.chatMsgModel;
import com.example.messaging_app.FirebaseDataModel.chatMsgModelForFirebase;
import com.example.messaging_app.FirebaseDataModel.getChatListToCachedInterface;
import com.example.messaging_app.FirebaseDataModel.getRecentChatForCachingInterface;
import com.example.messaging_app.FirebaseDataModel.getUserDetailsinterface;
import com.example.messaging_app.FirebaseDataModel.multipleChatsRemovedInterface;
import com.example.messaging_app.FirebaseDataModel.userDetailsModel;
import com.example.messaging_app.RoomDataModel.All_Chats_Dao;
import com.example.messaging_app.RoomDataModel.All_Chats_Entity;
import com.example.messaging_app.RoomDataModel.DataEntity;
import com.example.messaging_app.RoomDataModel.DatabaseDao;
import com.example.messaging_app.RoomDataModel.MessagingAppDatabase;
import com.example.messaging_app.userModels.usermodel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Repository {
    private DatabaseDao databaseDao;

    private All_Chats_Dao allChatsDao;

    public LiveData<List<All_Chats_Entity>> getChatList;
    public LiveData<List<DataEntity>> getAllInteractionsList;

    private final DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
    private final DatabaseReference chatsDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");

    private final String RECENT_CHATS_TO_CHACHED = "recent_chats_to_cached";


    public Repository(Application application){

        MessagingAppDatabase database = MessagingAppDatabase.getDbInstance(application);
        databaseDao = database.databaseDao();
        allChatsDao = database.allChatsDao();
        getAllInteractionsList = databaseDao.getInteractionList();


    }



    public void findPeopleInFirebaseDB(String mobileNumber, getUserDetailsinterface userDetailsInterface){

        FirebaseModel.FindPeopleinFirebase_RDB(mobileNumber, new getUserDetailsinterface() {
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

    public LiveData<List<DataEntity>> getAllInteractions(){

        return getAllInteractionsList;
    }

    public Observable<DataEntity> getUser(String interactedUsersNumber){
        return Observable.create(new ObservableOnSubscribe<DataEntity>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<DataEntity> emitter) throws Throwable {
                emitter.onNext(databaseDao.getUser(interactedUsersNumber));
            }
        });
    }


//    public Observable<LiveData<List<All_Chats_Entity>>> getAllChatsWithInteractedUser(String interactedUsersNumber){
//
//        return Observable.create(new ObservableOnSubscribe<LiveData<List<All_Chats_Entity>>>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<LiveData<List<All_Chats_Entity>>> emitter) throws Throwable {
//
//                DataEntity userEntity = databaseDao.getUser(interactedUsersNumber);
//
//                getChatList = allChatsDao.getChats(userEntity.getChat_key());
//
//                emitter.onNext(getChatList);
//
//            }
//        });
//
//    }






    // when app opens first time after installing

    public void getAllInteractionsAndTheirChats(String mobileNumber){

        FirebaseModel model = new FirebaseModel();
        model.getAllInteractedUsers(usersDatabaseReference,mobileNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ArrayList<userDetailsModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull ArrayList<userDetailsModel> userDetailsModels) {

                addAllinteractedUsersToRoomDB(userDetailsModels);
                Log.d("TESTING", "onNext: "+userDetailsModels.size());

                model.getAllChatsWithInteractedUsers(chatsDatabaseReference, userDetailsModels, mobileNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<ArrayList<ArrayList<chatMsgModel>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArrayList<ArrayList<chatMsgModel>> arrayLists) {
                        Log.d("TESTING", "onNext: "+arrayLists.size());
                        addAllInteractedUsersChatsToRoomDB(arrayLists);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void addAllinteractedUsersToRoomDB(ArrayList<userDetailsModel> userDetailsModels){

        for (userDetailsModel model : userDetailsModels){

            DataEntity entity = new DataEntity();
            entity.setInteracted_users_name(model.getUser_name());
            entity.setInteracted_users_number(model.getMobile_number());
            entity.setChat_key(model.getChat_key());

            DataEntity userEntity = databaseDao.getUser(entity.getInteracted_users_number());
            if (userEntity!=null){

                Log.d("TESTING", "addAllinteractedUsersToRoomDB: user already exist in room");
            }else {
                databaseDao.insert(entity);
                Log.d("TESTING", "addAllinteractedUsersToRoomDB: "+entity.getInteracted_users_name());
            }


        }

    }

    private void addAllInteractedUsersChatsToRoomDB(ArrayList<ArrayList<chatMsgModel>> allUsersChatLists){

        for (ArrayList<chatMsgModel> chatList : allUsersChatLists){

            for (chatMsgModel model : chatList){

                All_Chats_Entity entity = new All_Chats_Entity();
                entity.setChat_key(model.getChat_key());
                entity.setChat_msg(model.getChat_msg());
                entity.setUser_number(model.getUser_number());
                entity.setMsg_by(model.getMsg_by());
                Log.d("TESTING", "addAllInteractedUsersChatsToRoomDB: "+entity.getMsg_by());
                Log.d("TESTING", "addAllInteractedUsersChatsToRoomDB: "+entity.getChat_msg());
                allChatsDao.insert(entity);
            }

        }

    }

    // ============================================================================================================================//

    public void addChatsToRoomDb(String mobileNumber, String otherUsersnumber, chatMsgModelForFirebase chatMsgModelForFirebase){

        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.getChatKey(chatsDatabaseReference, mobileNumber, otherUsersnumber, new getRecentChatForCachingInterface() {
            @Override
            public void onSuccess(chatMsgModel model, String nodeKey) {

                Observable<chatMsgModel> observable = Observable.create(new ObservableOnSubscribe<chatMsgModel>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<chatMsgModel> emitter) throws Throwable {

                        chatMsgModel chatmodel = new chatMsgModel();
                        chatmodel.setChat_key(nodeKey);
                        chatmodel.setChat_msg(chatMsgModelForFirebase.getChat_msg());
                        chatmodel.setUser_number(chatMsgModelForFirebase.getUser_number());
                        chatmodel.setMsg_by(chatMsgModelForFirebase.getMsg_by());

                        emitter.onNext(chatmodel);

                    }
                });

                Observer<chatMsgModel> observer = new Observer<chatMsgModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull chatMsgModel model) {

                        All_Chats_Entity entity = new All_Chats_Entity();
                        entity.setUser_number(model.getUser_number());
                        entity.setChat_msg(model.getChat_msg());
                        entity.setChat_key(model.getChat_key());
                        entity.setMsg_by(model.getMsg_by());
                        Log.d("MSGSEND", "onNext: chat added to room");
                        allChatsDao.insert(entity);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };

                observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer);

            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

    }

    public void addChatsToFirebaseDB(String mobileNumber, String otherUsersNumber ,chatMsgModelForFirebase chatModel){

        FirebaseModel model = new FirebaseModel();
        model.addChatToFirebaseDB(chatsDatabaseReference, mobileNumber, otherUsersNumber, chatModel).subscribeOn(Schedulers.io()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d("MSGSEND", "onNext: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    public void addRecentChatInRoomDBForCaching(String mobileNumber){

        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.getRecentChatForCaching(chatsDatabaseReference, mobileNumber, new getRecentChatForCachingInterface() {
            @Override
            public void onSuccess(chatMsgModel model, String nodeKey) {
                removeRecentChatsFromFirebaseDB(mobileNumber, nodeKey);
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        }).subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                subscribe(new Observer<chatMsgModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull chatMsgModel model) {

                All_Chats_Entity entity = new All_Chats_Entity();
                entity.setChat_key(model.getChat_key());
                entity.setChat_msg(model.getChat_msg());
                entity.setMsg_by(model.getMsg_by());
                entity.setUser_number(model.getUser_number());


                allChatsDao.insert(entity);
                DataEntity userEntity = databaseDao.getUser(entity.getUser_number());

                if (userEntity!=null){
                    if (!checkUserLastInRoomDBListOrNot(userEntity.getInteracted_users_number())){
                        replaceUserWithLastUserInRoomDB(userEntity.getInteracted_users_number());
                    }
                    Log.d("MSG", "onNext: user already added to room");

                }else {
                    userDetailsModel userDetailsModel = new userDetailsModel();
                    userDetailsModel.setUser_name(entity.getMsg_by());
                    userDetailsModel.setMobile_number(entity.getUser_number());
                    userDetailsModel.setChat_key(entity.getChat_key());

                    addRecentInteractionToFirebase(mobileNumber ,userDetailsModel );

                    DataEntity dataEntity = new DataEntity();
                    dataEntity.setChat_key(model.getChat_key());
                    dataEntity.setInteracted_users_number(model.getUser_number());
                    dataEntity.setInteracted_users_name(model.getMsg_by());


                    databaseDao.insert(dataEntity);

                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    // called when user opens his app
    public void addAllChatsForCachingInRoomDB(String mobileNumber, boolean openFirstTimeAfterInstall){

        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.getAllChatForCaching(chatsDatabaseReference, mobileNumber, new getChatListToCachedInterface() {
            @Override
            public void onSuccess(ArrayList<String> nodeKeyList) {

                removeMultiplechatsFromFirebaseDB(mobileNumber, nodeKeyList, new multipleChatsRemovedInterface() {
                    @Override
                    public void onSuccess() {

                        // when new chats added after app is opened this will add them in room with their interacted user model
                        addRecentChatInRoomDBForCaching(mobileNumber);

                    }
                });
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        }).subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                subscribe(new Observer<ArrayList<chatMsgModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull ArrayList<chatMsgModel> arrayList) {

                if (!openFirstTimeAfterInstall){

                    ArrayList<userDetailsModel> userList = new ArrayList<>();
                    for (chatMsgModel model : arrayList){

                        All_Chats_Entity entity = new All_Chats_Entity();
                        entity.setChat_key(model.getChat_key());
                        entity.setChat_msg(model.getChat_msg());
                        entity.setMsg_by(model.getMsg_by());
                        entity.setUser_number(model.getUser_number());

                        allChatsDao.insert(entity);

                        DataEntity userEntity = databaseDao.getUser(entity.getUser_number());
                        if (userEntity!=null){
                            if (!checkUserLastInRoomDBListOrNot(userEntity.getInteracted_users_number())){
                                replaceUserWithLastUserInRoomDB(userEntity.getInteracted_users_number());
                            }

                        }else {

                            userDetailsModel userDetailsModel = new userDetailsModel();
                            userDetailsModel.setUser_name(entity.getMsg_by());
                            userDetailsModel.setMobile_number(entity.getUser_number());
                            userDetailsModel.setChat_key(entity.getChat_key());

                            userList.add(userDetailsModel);

                            DataEntity dataEntity = new DataEntity();
                            dataEntity.setChat_key(model.getChat_key());
                            dataEntity.setInteracted_users_number(model.getUser_number());
                            dataEntity.setInteracted_users_name(model.getMsg_by());

                            databaseDao.insert(dataEntity);

                        }

                    }


                    addMultipleInteractionToFirebase(mobileNumber, userList);


                }else {

                    firebaseModel.getAllInteractedUsers(usersDatabaseReference, mobileNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(new Observer<ArrayList<userDetailsModel>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ArrayList<userDetailsModel> userDetailsModels) {
                            if (userDetailsModels.size()==0){
                                ArrayList<userDetailsModel> userList = new ArrayList<>();
                                for (chatMsgModel model : arrayList){

                                    All_Chats_Entity entity = new All_Chats_Entity();
                                    entity.setChat_key(model.getChat_key());
                                    entity.setChat_msg(model.getChat_msg());
                                    entity.setMsg_by(model.getMsg_by());
                                    entity.setUser_number(model.getUser_number());

                                    allChatsDao.insert(entity);

                                    DataEntity userEntity = databaseDao.getUser(entity.getUser_number());
                                    if (userEntity!=null){
                                        if (!checkUserLastInRoomDBListOrNot(userEntity.getInteracted_users_number())){
                                            replaceUserWithLastUserInRoomDB(userEntity.getInteracted_users_number());
                                        }

                                    }else {

                                        userDetailsModel userDetailsModel = new userDetailsModel();
                                        userDetailsModel.setUser_name(entity.getMsg_by());
                                        userDetailsModel.setMobile_number(entity.getUser_number());
                                        userDetailsModel.setChat_key(entity.getChat_key());

                                        userList.add(userDetailsModel);

                                        DataEntity dataEntity = new DataEntity();
                                        dataEntity.setChat_key(model.getChat_key());
                                        dataEntity.setInteracted_users_number(model.getUser_number());
                                        dataEntity.setInteracted_users_name(model.getMsg_by());

                                        databaseDao.insert(dataEntity);

                                    }

                                }


                                addMultipleInteractionToFirebase(mobileNumber, userList);

                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

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

    }


    public void addRecentInteractionToRoomDBAndFirebaseDB(String mobileNumber, usermodel interactedUserDetails){
        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.getChatKey(chatsDatabaseReference, mobileNumber, interactedUserDetails.getMobile_number(), new getRecentChatForCachingInterface() {
            @Override
            public void onSuccess(chatMsgModel model, String nodeKey) {
                Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        DataEntity entity = databaseDao.getUser(interactedUserDetails.getMobile_number());
                        if (entity==null){
                            entity = new DataEntity();
                            entity.setInteracted_users_name(interactedUserDetails.getUser_name());
                            entity.setInteracted_users_number(interactedUserDetails.getMobile_number());
                            entity.setChat_key(nodeKey);

                            databaseDao.insert(entity);

                            userDetailsModel userModel = new userDetailsModel();
                            userModel.setChat_key(entity.getChat_key());
                            userModel.setUser_name(entity.getInteracted_users_name());
                            userModel.setMobile_number(entity.getInteracted_users_number());

                            addRecentInteractionToFirebase(mobileNumber, userModel);

                            emitter.onNext("user added to room db and Firebase");
                        }
                    }
                });

                Observer<String> observer = new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d("MSGSEND", "onNext: "+s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer);

            }

            @Override
            public void onFailure(DatabaseError error) {
                Log.d("MSGSEND", "onFailure: "+error);
            }
        });

    }



// ===================================================================================================//



    public void removeRecentChatsFromFirebaseDB(String mobileNumber, String nodeKey){
        chatsDatabaseReference.child(mobileNumber).child(RECENT_CHATS_TO_CHACHED).child(nodeKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("MSG", "onSuccess: removed"+ nodeKey);
            }
        });
    }

    public void removeMultiplechatsFromFirebaseDB(String mobileNumber, ArrayList<String> nodeKeyList, multipleChatsRemovedInterface chatsRemovedInterface){


        for (String nodeKey : nodeKeyList){
            chatsDatabaseReference.child(mobileNumber).child(RECENT_CHATS_TO_CHACHED).child(nodeKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("MSG", "onSuccess: removed");
                }
            });

        }
        chatsRemovedInterface.onSuccess();

    }



    public void addRecentInteractionToFirebase(String mobileNumber, userDetailsModel model){
        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.addRecentInteractionToFirebaseDB(usersDatabaseReference, mobileNumber, model).subscribeOn(Schedulers.io()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d("MSG", "onNext: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void addMultipleInteractionToFirebase(String mobileNumber, ArrayList<userDetailsModel> interactedUsersList){
        FirebaseModel model = new FirebaseModel();
        model.addmultipleInteractionsToFirebaseDB(usersDatabaseReference, mobileNumber, interactedUsersList).subscribeOn(Schedulers.io()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d("MSG", "onNext: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    public boolean checkUserLastInRoomDBListOrNot(String interactedUsersNumber){
        DataEntity userEntity = databaseDao.getLastUserInRoomDB();
        if (Objects.equals(userEntity.getInteracted_users_number(), interactedUsersNumber)){
            return true;
        }else {
            return false;
        }

    }

    public void replaceUserWithLastUserInRoomDB(String interactedUsersNumber){
        DataEntity userEntity = databaseDao.getUser(interactedUsersNumber);
        DataEntity lastUserEntity = databaseDao.getLastUserInRoomDB();

        databaseDao.delete(userEntity);
        int lastUsersId = lastUserEntity.getId();
        userEntity.setId(lastUsersId+1);

        databaseDao.insert(userEntity);

    }

    public void getChatKey( String mobileNumber, String otherUsersNumber, getRecentChatForCachingInterface chatForCachingInterface){

        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.getChatKey(chatsDatabaseReference, mobileNumber, otherUsersNumber, new getRecentChatForCachingInterface() {
            @Override
            public void onSuccess(chatMsgModel model, String nodeKey) {
                chatForCachingInterface.onSuccess(null, nodeKey);
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

    }





}
