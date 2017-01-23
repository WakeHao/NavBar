package com.wakehao.demo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wakehao.demo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeChatMineFragment extends Fragment {


    public WeChatMineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_we_chat_mine, container, false);
    }

}
