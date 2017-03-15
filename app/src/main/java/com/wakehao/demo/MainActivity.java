package com.wakehao.demo;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;
import com.wakehao.bar.BottomNavigationBar;

import com.wakehao.bar.BottomNavigationItemWithDot;
import com.wakehao.demo.fragment.LoginActivity;
import com.wakehao.demo.fragment.WeChatContactFragment;
import com.wakehao.demo.fragment.WeChatFindFragment;
import com.wakehao.demo.fragment.WeChatHomeFragment;
import com.wakehao.demo.fragment.WeChatMineFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = (BottomNavigationBar) findViewById(R.id.bar);
        //可动态改变item的标题
//        bar.setTitle(0,"home(99)");
        bar.showNum(0,80);
        bar.showNum(1,100);
        bar.showNum(2,-2);
        bar.disMissNum(3);

        bar.setOnNavigationItemSelectedListener(new BottomNavigationBar.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull BottomNavigationItemWithDot item, int selectedPosition) {
                if(selectedPosition==3){

//                    startActivityForResult(new Intent(MainActivity.this,LoginActivity.class),1);
                    //用户切换item
                    bar.setItemSelected(3,true);
                    //返回false表示不响应点击事件
                    return false;
                }
                else return true;
            }

            @Override
            public void onNavigationItemSelectedAgain(@NonNull BottomNavigationItemWithDot item, int reSelectedPosition) {

                //连续点击了同一个reSelectedPosition位置的item
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK||requestCode!=1)return;
        //不带动画的切换item
        bar.setItemSelected(3,false);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public BottomNavigationBar getBar(){
        return bar;
    }
}
