package com.wakehao.demo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.wakehao.bar.BottomNavigationBar;
import com.wakehao.bar.BottomNavigationItem;
import com.wakehao.demo.fragment.LoginActivity;
import com.wakehao.demo.fragment.WeChatContactFragment;
import com.wakehao.demo.fragment.WeChatFindFragment;
import com.wakehao.demo.fragment.WeChatHomeFragment;
import com.wakehao.demo.fragment.WeChatMineFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = (BottomNavigationBar) findViewById(R.id.bar);

        bar.showNum(0,80);
        bar.showNum(1,100);
        bar.showNum(2,-2);
        bar.disMissNum(3);
    }

}
