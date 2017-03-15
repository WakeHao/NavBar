package com.wakehao.demo.fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wakehao.bar.BottomNavigationBar;
import com.wakehao.demo.MainActivity;
import com.wakehao.demo.R;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view){
        setResult(RESULT_OK);
        finish();
    }

}
