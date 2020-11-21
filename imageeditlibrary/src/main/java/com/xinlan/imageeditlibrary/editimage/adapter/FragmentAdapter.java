package com.xinlan.imageeditlibrary.editimage.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;
    private int positionList=-1;
    public FragmentAdapter(@NonNull FragmentManager fm, List<Fragment>fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("FragmentAdapter", String.valueOf(position));
        if (positionList != -1) {
            return fragmentList.get(positionList);
        } else {
            return fragmentList.get(position);
        }
    }

    public void setPosition(int position) {
        positionList = position;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
