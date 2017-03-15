package com.wakehao.demo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wakehao.bar.BottomNavigationBar;
import com.wakehao.demo.CustomScrollView;
import com.wakehao.demo.MainActivity;
import com.wakehao.demo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeChatContactFragment extends Fragment {


    public WeChatContactFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_we_chatc_contact, container, false);

        return view;
    }

}
