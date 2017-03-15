package com.wakehao.demo;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.wakehao.bar.BarUtils;

public class IconActivity extends AppCompatActivity {

    private @ColorInt int selectedColor;
    private @ColorInt int unSelectedColor;
    private ImageView image_unSelected;
    private ImageView image_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);

        initColor() ;


        image_unSelected = (ImageView) findViewById(R.id.image_unSelected);
        image_selected = (ImageView) findViewById(R.id.image_selected);

        SeekBar seek= (SeekBar) findViewById(R.id.seek);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float offset=1-progress/100f;
                image_unSelected.setColorFilter(BarUtils.getIconColor(offset, Color.TRANSPARENT, selectedColor, unSelectedColor, 10), PorterDuff.Mode.SRC_IN);
                image_selected.setColorFilter(BarUtils.getIconColor(offset,selectedColor, Color.TRANSPARENT, Color.TRANSPARENT, 10), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        image_selected.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
    }


    private void initColor() {
         selectedColor= getResources().getColor(R.color.green);
         unSelectedColor=Color.GRAY;
    }


}
