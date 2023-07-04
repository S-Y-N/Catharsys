package com.example.catarsys.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.catarsys.Fragments.CallsFragment;
import com.example.catarsys.Fragments.ChatFragment;
import com.example.catarsys.Fragments.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter
{

    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position ==0 ){
            return new ChatFragment();
        }else if(position ==1){
            return new StatusFragment();
        }else if(position == 2){
            return new CallsFragment();
        }else{
            return new ChatFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if(position == 0) {
            title ="CHATS";
        }
        if(position == 1){
            title ="STATUS";
        }
        if(position == 2){
            title="CALLS";
        }
        return title;
    }
}
