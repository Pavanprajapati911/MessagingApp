package com.example.messaging_app.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messaging_app.R;
import com.example.messaging_app.RoomDataModel.DataEntity;

import java.util.ArrayList;
import java.util.List;

public class mainActivityAdapter extends RecyclerView.Adapter<mainActivityAdapter.viewmodel> {

    List<DataEntity> userlist = new ArrayList<>();
    OnClick onclick;

    public mainActivityAdapter(OnClick onclick){
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public viewmodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_people_rv_layout, parent, false);

        return new viewmodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewmodel holder, @SuppressLint("RecyclerView") int position) {

        holder.mobilenumber.setText(userlist.get(position).getInteracted_users_number());
        holder.name.setText(userlist.get(position).getInteracted_users_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.onClick(userlist.get(position).getInteracted_users_name(), userlist.get(position).getInteracted_users_number());

            }
        });

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public interface OnClick{
        void onClick(String otherUserName, String otherUserNumber);
    }

    public void setUserlist(List<DataEntity> userlist){
        this.userlist = userlist;
        notifyDataSetChanged();
    }

    public class viewmodel extends RecyclerView.ViewHolder{
        TextView name;
        TextView mobilenumber;
        public viewmodel(@NonNull View itemView) {
            super(itemView);

           name = itemView.findViewById(R.id.find_people_rv_username);
           mobilenumber = itemView.findViewById(R.id.find_people_rv_usernumber);

        }
    }

}
