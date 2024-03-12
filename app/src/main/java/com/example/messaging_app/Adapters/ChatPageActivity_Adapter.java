package com.example.messaging_app.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messaging_app.R;
import com.example.messaging_app.RoomDataModel.All_Chats_Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatPageActivity_Adapter extends RecyclerView.Adapter {

    String mobileNumber;

    int MSG_SENT_BY_MAINUSER = 1;
    int MSG_SENT_BY_OTHERUSER = 2;
    List<All_Chats_Entity> chatList = new ArrayList<>();

    public ChatPageActivity_Adapter(String mobileNumber){
        this.mobileNumber = mobileNumber;
    }

    @Override
    public int getItemViewType(int position) {

        if (Objects.equals(chatList.get(position).getUser_number(), mobileNumber)){
            return MSG_SENT_BY_MAINUSER;
        }else{
            return MSG_SENT_BY_OTHERUSER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("RECY", "onCreateViewHolder: "+mobileNumber);
        View view;
        if (viewType==MSG_SENT_BY_MAINUSER){
            Log.d("RECY", "onCreateViewHolder: if");
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatmsg_layout_of_mainuser, parent, false);
               return new viewHolder(view);
        }else {
            Log.d("RECY", "onCreateViewHolder: else");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatmsg_layout_of_otheruser, parent, false);
            return new viewHolderForOtherUSer(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==MSG_SENT_BY_MAINUSER){
            ((viewHolder)holder).msgByMainUser.setText(chatList.get(position).getChat_msg());
        }else {
            ((viewHolderForOtherUSer)holder).msgByOtherUser.setText(chatList.get(position).getChat_msg());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void setChatList(List<All_Chats_Entity> chatList){
        if (chatList!=null){
            this.chatList = chatList;
        }else {
            this.chatList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView msgByMainUser;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            msgByMainUser = itemView.findViewById(R.id.mainuserchatmsgtextview);

        }
    }

    public class viewHolderForOtherUSer extends RecyclerView.ViewHolder{
        TextView msgByOtherUser;
        public viewHolderForOtherUSer(@NonNull View itemView) {
            super(itemView);

            msgByOtherUser = itemView.findViewById(R.id.chatmsgtextview);

        }
    }

}
