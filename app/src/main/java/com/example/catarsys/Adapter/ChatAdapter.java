package com.example.catarsys.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catarsys.ChatingActivity;
import com.example.catarsys.Models.Message;
import com.example.catarsys.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<Message> _msgModel;
    Context _context;
    String receiverId;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;


    public ChatAdapter(ArrayList<Message> _msgModel, Context _context) {
        this._msgModel = _msgModel;
        this._context = _context;
    }

    public ChatAdapter(ArrayList<Message> _msgModel, Context _context, String receiverId) {
        this._msgModel = _msgModel;
        this._context = _context;
        this.receiverId = receiverId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(_context).inflate(R.layout.layout_sender,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view  = LayoutInflater.from(_context).inflate(R.layout.layout_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(_msgModel.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = _msgModel.get(position);
        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).senderMsg.setText(msg.getMessage());
        }else{
            ((ReceiverViewHolder)holder).receiveMsg.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return _msgModel.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiveMsg, receiveTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiveMsg = itemView.findViewById(R.id.receiver_text);
            receiveTime = itemView.findViewById(R.id.receiver_time);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }
}
