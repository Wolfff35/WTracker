package com.wolff.wtracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wolff.wtracker.R;

/**
 * Created by wolff on 05.10.2017.
 */

public class Register_user_fragment extends Fragment{
    public static Register_user_fragment newInstance(){
        Register_user_fragment fragment = new Register_user_fragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.google_map_fragment, container, false);
        return view;
    }
}
