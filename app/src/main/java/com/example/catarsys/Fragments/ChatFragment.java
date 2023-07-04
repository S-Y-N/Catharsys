package com.example.catarsys.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.catarsys.Adapter.UsersAdapter;
import com.example.catarsys.Models.Users;
import com.example.catarsys.R;
import com.example.catarsys.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    FragmentChatBinding _binding;
    ArrayList<Users> _list = new ArrayList<>();
    FirebaseDatabase _db;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _binding = FragmentChatBinding.inflate(inflater,container,false);
        _db = FirebaseDatabase.getInstance();

        UsersAdapter adapter = new UsersAdapter(_list,getContext());
        _binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        _binding.chatRecyclerView.setLayoutManager(layoutManager);

        _db.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    //показывать в списке контактов, всех кроме юзера
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        _list.add(users);
                    }
                }
                //заменить на что то более оптимальное, создается новый адаптер для списка, когда перерис
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return _binding.getRoot();
    }
}