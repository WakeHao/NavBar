package com.wakehao.demo;

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

        bar.setOnNavigationItemSelectedListener(new BottomNavigationBar.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull BottomNavigationItem item, int selectedPosition) {
                if(selectedPosition==2){
//                    bar.setItemSelected(2);
//                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),1);
//                    bar.setItemSelected(2);
                    return true;
                }
                return true;
            }

            @Override
            public void onNavigationItemSelectedAgain(@NonNull BottomNavigationItem item, int reSelectedPosition) {

            }
        });

        bar.showNum(1,80);
        bar.showNum(0,7);
        bar.disMissNum(1);
//        bar.setItemSelected(2);
//        bar.setItemSelected(3);

//
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK)return;
        bar.setItemSelected(2);
    }
}
