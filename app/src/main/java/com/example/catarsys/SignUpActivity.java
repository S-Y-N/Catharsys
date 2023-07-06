package com.example.catarsys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.catarsys.Models.Users;
import com.example.catarsys.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding _binding;
    private FirebaseAuth _auth;
    FirebaseDatabase _db;
    ProgressDialog _dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       _binding = ActivitySignUpBinding.inflate(getLayoutInflater());
       setContentView(_binding.getRoot());

       _auth = FirebaseAuth.getInstance();
       _db = FirebaseDatabase.getInstance();

        _dialog = new ProgressDialog(SignUpActivity.this);
        _dialog.setTitle("Creating Account");
        _dialog.setMessage("Creating your account");

        _binding.alreadyAcc.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        _binding.btnSignUp.setOnClickListener(view -> {
            if(!_binding.txtUsername.getText().toString().isEmpty()
                &&!_binding.txtEmail.getText().toString().isEmpty()
                &&!_binding.txtPassword.getText().toString().isEmpty())
                {
                _dialog.show();
                _auth.createUserWithEmailAndPassword(_binding.txtEmail.getText().toString(),_binding.txtPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        //off dialog
                        _dialog.dismiss();
                        if(task.isSuccessful()){
                            //if all is oK = create new user object
                            Users user = new Users(_binding.txtUsername.getText().toString(),_binding.txtEmail.getText().toString(),_binding.txtPassword.getText().toString());
                            //set user uId
                            String id = task.getResult().getUser().getUid();
                            //create table = firebase is nosql db - main field is uid with all user`s fields
                            //get references fot root node and their child
                            _db.getReference().child("Users").child(id).setValue(user);

                            Toast.makeText(SignUpActivity.this,"Sign Up Successful",Toast.LENGTH_SHORT).show();
                            Log.d("SignUpActivity","onclick: signUp success");
                            clearEditView();
                            //переход к форме входа = сделать плавный переход
                            finish();
                        }else{
                            Log.d("SignUpActivity","onclick: signUp error");
                            Toast.makeText(SignUpActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
            }else{
                Toast.makeText(SignUpActivity.this,"Enter Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearEditView(){
        _binding.txtEmail.getText().clear();
        _binding.txtUsername.getText().clear();
        _binding.txtPassword.getText().clear();
    }
}