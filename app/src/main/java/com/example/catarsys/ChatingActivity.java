package com.example.catarsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.catarsys.Adapter.ChatAdapter;
import com.example.catarsys.Models.Message;
import com.example.catarsys.databinding.ActivityChatingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatingActivity extends AppCompatActivity {
    ActivityChatingBinding _bind;
    FirebaseDatabase _db;
    FirebaseAuth _auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _bind = ActivityChatingBinding.inflate(getLayoutInflater());
        setContentView(_bind.getRoot());

        _auth = FirebaseAuth.getInstance();
        _db = FirebaseDatabase.getInstance();

        final String senderId = _auth.getUid();
        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        _bind.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.man).into(_bind.profileImage);

        _bind.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ChatingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        final ArrayList<Message> messages = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messages,this,receiveId);

        _bind.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        _bind.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiveId;
        final String receiverRoom = receiveId  +senderId;


        _db.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message model = snapshot1.getValue(Message.class);
                            model.setMessageId(snapshot1.getKey());
                            messages.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        /////[START: SEND MESSAGE FUNCTION]
        _bind.send.setOnClickListener(view -> {
            String message = _bind.enterMessage.getText().toString();
            final Message model = new Message(senderId,message);
            model.setTimestamp(new Date().getTime());
            _bind.enterMessage.setText("");

            _db.getReference().child("Chats")
                    .child(senderRoom)
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            _db.getReference().child("Chats")
                                    .child(receiverRoom)
                                    .push()
                                    .setValue(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                        }
                    });
        });
        /////[END: SEND MESSAGE FUNCTION]
    }
}