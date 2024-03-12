package com.example.messaging_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messaging_app.FirebaseDataModel.userDetailsModel;
import com.example.messaging_app.R;
import com.example.messaging_app.userModels.usermodel;

import java.util.ArrayList;


public class Find_People_Activity_RvAdapter extends RecyclerView.Adapter<Find_People_Activity_RvAdapter.viewHolder> {
    setOnCLick setOnCLick;
    ArrayList<usermodel> userList;

    public Find_People_Activity_RvAdapter(ArrayList<usermodel> userList, setOnCLick setOnCLick){
        this.userList = userList;
        this.setOnCLick = setOnCLick;
    }

    public interface setOnCLick {
        void onClick();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_people_rv_layout, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.usernumber.setText(userList.get(position).getMobile_number());
        holder.username.setText(userList.get(position).getUser_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnCLick.onClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView usernumber;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.find_people_rv_username);
            usernumber = itemView.findViewById(R.id.find_people_rv_usernumber);

        }
    }

}
