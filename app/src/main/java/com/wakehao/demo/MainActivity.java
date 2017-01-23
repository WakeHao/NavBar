package com.wakehao.demo;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wakehao.bar.BottomNavigationBar;
import com.wakehao.bar.BottomNavigationItem;
import com.wakehao.demo.fragment.WeChatContactFragment;
import com.wakehao.demo.fragment.WeChatFindFragment;
import com.wakehao.demo.fragment.WeChatHomeFragment;
import com.wakehao.demo.fragment.WeChatMineFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bar;
    private ViewPager viewpager;
    private WePagerAdapter wePagerAdapter;

    private @ColorInt int testColor=-12140773;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bar = (BottomNavigationBar) findViewById(R.id.bar);

//        bar.setBackgroundColor(testColor);
//        bar.setOnNavigationItemSelectedListener(new BottomNavigationBar.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull BottomNavigationItem item, int selectedPosition) {
//                return selectedPosition==2?false:true;
//            }
//        });

        viewpager = (ViewPager) findViewById(R.id.viewpager);


        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset>0){
                    bar.startAlphaAnim(position,positionOffset);
                }
            }
        });
        bar.setOnNavigationItemSelectedListener(new BottomNavigationBar.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull BottomNavigationItem item, int selectedPosition) {
                viewpager.setCurrentItem(selectedPosition,false);
                return true;
            }
        });

        WeChatHomeFragment weChatHomeFragment=new WeChatHomeFragment();
        WeChatContactFragment weChatContactFragment=new WeChatContactFragment();
        WeChatFindFragment weChatFindFragment=new WeChatFindFragment();
        WeChatMineFragment weChatMineFragment=new WeChatMineFragment();

        List<Fragment> fragments=new ArrayList<>();
        fragments.add(weChatHomeFragment);
        fragments.add(weChatContactFragment);
        fragments.add(weChatFindFragment);
        fragments.add(weChatMineFragment);
        wePagerAdapter = new WePagerAdapter(getSupportFragmentManager(),fragments);

        viewpager.setAdapter(wePagerAdapter);

    }
}
