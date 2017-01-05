package com.wakehao.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class BottomNavigationBar extends LinearLayout{


    public BottomNavigationBar(Context context) {
        super(context,null);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.BottomNavigationBar,defStyleAttr,R.style.BottomNavigationView);
        if(typedArray.hasValue(R.styleable.BottomNavigationBar_menu)){
            ItemParser parser=new ItemParser(context);
            parser.parser(typedArray.getResourceId(R.styleable.BottomNavigationBar_menu,0));
            List<BottomNavigationItem> bottomNavigationItems = parser.getBottomNavigationItems();

        }


        typedArray.recycle();

    }

}
