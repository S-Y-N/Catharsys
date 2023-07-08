package com.example.catarsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.catarsys.Adapter.FragmentAdapter;
import com.example.catarsys.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding _binding;
    FirebaseAuth _auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _auth = FirebaseAuth.getInstance();
        _binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

        _binding.tabLayout.setupWithViewPager(_binding.viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings){
            Intent sett = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(sett);
        }else if(id == R.id.groupChat){
            Toast.makeText(this,"Group Chat",Toast.LENGTH_SHORT).show();
        }else if(id == R.id.logout){
            _auth.signOut();
            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}