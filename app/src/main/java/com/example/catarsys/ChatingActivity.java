package com.example.catarsys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.catarsys.databinding.ActivityChatingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

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
        String reciever = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        _bind.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.man).into(_bind.profileImage);

        _bind.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}