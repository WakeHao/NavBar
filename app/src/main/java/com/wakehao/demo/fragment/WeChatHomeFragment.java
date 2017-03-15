package com.wakehao.demo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
public class WeChatHomeFragment extends Fragment {


    public WeChatHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_we_chat_home, container, false);
        CustomScrollView customScrollView= (CustomScrollView) view.findViewById(R.id.scroll);
        final BottomNavigationBar bar = ((MainActivity) getActivity()).getBar();
        customScrollView.setScrollStateListener(new CustomScrollView.ScrollStateListener() {
            @Override
            public void onScrollUp() {
                bar.hideBar();
            }

            @Override
            public void onScrollDown() {
                bar.showBar();
            }
        });
        return view;
    }

}
