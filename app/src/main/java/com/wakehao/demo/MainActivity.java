package com.wakehao.demo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wakehao.bar.BottomNavigationBar;
import com.wakehao.bar.BottomNavigationItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationBar bar= (BottomNavigationBar) findViewById(R.id.bar);

//        bar.setOnNavigationItemSelectedListener(new BottomNavigationBar.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull BottomNavigationItem item, int selectedPosition) {
//                return selectedPosition==2?false:true;
//            }
//        });
    }
}
