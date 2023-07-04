package com.example.catarsys;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.catarsys.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding _binding;
    ProgressDialog _dialog;
    FirebaseAuth _auth;
    FirebaseDatabase _db;

    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        TextView signUp = findViewById(R.id.txtSignUp);
        signUp.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this,SignUpActivity.class)));


        _dialog = new ProgressDialog(SignInActivity.this);
        _dialog.setTitle("Login");
        _dialog.setMessage("Check in progress");

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        _auth = FirebaseAuth.getInstance();
        _db = FirebaseDatabase.getInstance();

        //[START : SIGN IN BY THE EMAIL AND PASSWORD]
        _binding.btnSignIn.setOnClickListener(view -> {
            if(!_binding.txtEmail.getText().toString().isEmpty()
                    &&!_binding.txtPassword.toString().isEmpty()){

                _dialog.show();
                _auth.signInWithEmailAndPassword(_binding.txtEmail.getText().toString(),_binding.txtPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            _dialog.dismiss();
                            if(task.isSuccessful()){
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(SignInActivity.this,"Welcome!",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SignInActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                Log.d("SignIn onClick","Success sign in");
            }else{
                Toast.makeText(SignInActivity.this,"Enter your data",Toast.LENGTH_SHORT).show();
                Log.d("SignIn onClick","Error with sign in");
            }
        });
        //[END : SIGN IN BY THE EMAIL AND PASSWORD]

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            try {
                                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                                String idToken = credential.getGoogleIdToken();
                                if (idToken !=  null) {
                                    String email = credential.getId();
                                    Toast.makeText(getApplicationContext(),"Email"+email,Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "Got ID token.");
                                }
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        _binding.btnGoogle.setOnClickListener(view -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(SignInActivity.this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(SignInActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d("TAG", e.getLocalizedMessage());
                    }
                }));
        if(_auth.getCurrentUser()!=null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}