package com.example.catarsys.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catarsys.ChatingActivity;
import com.example.catarsys.Models.Users;
import com.example.catarsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_template,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar3).into(holder.image);
        holder.userName.setText(users.getUserName());

        //Отображение последнего сообщения в чате
        //Получаем объект бд и связываем с таблицей Chats и с ИД пользователя, сортируем по времени и получаем последнее
        FirebaseDatabase.getInstance().getReference().child("Chats")
                    .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                    .orderByChild("timestamp")
                    .limitToLast(1)
                    //addListenerForSingleValueEvent - обращается к БД 1 раз, выполняет запрос ( сортировка и получение) и перестает слушать
                    //получаем снимок(срез) данных с БД  и присваимаем значение - последнее сообщение в поле
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                for(DataSnapshot snapshot1:snapshot.getChildren()){
                                    holder.lastMessage.setText(snapshot1.child("message").getValue().toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        holder.itemView.setOnClickListener(view -> {
            Intent intent  = new Intent(context, ChatingActivity.class);
            intent.putExtra("userId",users.getUserId());
            intent.putExtra("profilePic",users.getProfilePic());
            intent.putExtra("userName", users.getUserName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView userName, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image =itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
