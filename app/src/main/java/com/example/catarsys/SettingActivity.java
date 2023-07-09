package com.example.catarsys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.catarsys.Models.Users;
import com.example.catarsys.databinding.ActivitySettingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding _bind;
    FirebaseAuth _auth;
    FirebaseDatabase _db;
    FirebaseStorage _storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _bind = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(_bind.getRoot());

        //initialize Firebase modules
        _auth = FirebaseAuth.getInstance();
        _db = FirebaseDatabase.getInstance();
        _storage = FirebaseStorage.getInstance();

        //move to main activity from setting
        _bind.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
        });
        _db.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfilePic())
                                .placeholder(R.drawable.avatar3)
                                .into(_bind.profileImage);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //show status in profile
        //take data from db and insert in fields
        _db.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users user = snapshot.getValue(Users.class);
                            _bind.txtUsername.setText(user.getUserName());
                            _bind.txtStatus.setText(user.getStatus());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        _bind.saveStatus.setOnClickListener(view -> {
            if(!_bind.txtUsername.getText().toString().isEmpty() && !_bind.saveStatus.getText().toString().isEmpty()){
                String username = _bind.txtUsername.getText().toString();
                String status = _bind.txtStatus.getText().toString();

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName",username);
                obj.put("status",status);

                _db.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(SettingActivity.this, "Profile Updated",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SettingActivity.this,"Enter something for update",Toast.LENGTH_SHORT).show();
            }

        });

        //add profile picture
        _bind.addProfilePic.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,25);
        });

        _bind.privacy.setOnClickListener(view -> Toast.makeText(SettingActivity.this, "Privacy Page",Toast.LENGTH_SHORT).show());
        _bind.aboutUs.setOnClickListener(view -> Toast.makeText(SettingActivity.this, "About Us Page",Toast.LENGTH_SHORT).show());
        _bind.help.setOnClickListener(view -> Toast.makeText(SettingActivity.this, "Help Page",Toast.LENGTH_SHORT).show());
        _bind.logout.setOnClickListener(view -> {
            _auth.signOut();
            Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){
            Uri pathFile = data.getData();
            _bind.profileImage.setImageURI(pathFile);

            final StorageReference reference = _storage.getReference().child("profile_pic")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(pathFile).addOnSuccessListener(taskSnapshot -> {
               reference.getDownloadUrl().addOnSuccessListener(uri -> {
                   _db.getReference().child("Users")
                       .child(FirebaseAuth.getInstance().getUid())
                       .child("profilePic").setValue(uri.toString());
               });
            });
        }
    }
}