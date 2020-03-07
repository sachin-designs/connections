package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<Message_ref> lmessages;
    private String connect_user;

    public MessageAdapter(List<Message_ref> messages, String connect_username) {
        this.connect_user=connect_username;
        this.lmessages = messages;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView message_left;
        public LinearLayout.LayoutParams params;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            message_left = (TextView) itemView.findViewById(R.id.message_box);
            //messageButton.setOnClickListener(this); // future purpose.
        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.message_rc_items_right, parent, false);
        // Return a new holder instance
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.params = (LinearLayout.LayoutParams) holder.message_left.getLayoutParams();
        final Message_ref msg_obj = lmessages.get(position);
        String msg_user=msg_obj.Message_user;
        if(msg_user==connect_user){
            holder.params.gravity = Gravity.START;
            holder.message_left.setTextColor(Color.BLUE);
            holder.message_left.setText(msg_obj.Message);
        } else {
            holder.params.gravity=Gravity.END;
            holder.message_left.setTextColor(Color.GREEN);
            holder.message_left.setText(msg_obj.Message);
        }
    }

    @Override
    public int getItemCount() {
        return lmessages.size();
    }
}
