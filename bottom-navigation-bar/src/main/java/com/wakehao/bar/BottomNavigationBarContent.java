package com.wakehao.bar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by WakeHao on 2017/1/10.
 */

public class BottomNavigationBarContent extends LinearLayout {

    private final int mBottomNavigationBarHeight;

    private final int mActiveItemMaxWidth;
    private final int mActiveItemMinWidth;

    private final int mInactiveItemMaxWidth;
    private final int mInactiveItemMinWidth;


    private int mActivePosition=0;
    private OnClickListener mOnClickListener;
    private int[] widthSpec;
    private int mSwitchMode;


    public BottomNavigationBarContent(Context context) {
        this(context,null);
    }


    private BottomNavigationBarContent(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    private BottomNavigationBarContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources res=getResources();

        mActiveItemMaxWidth=res.getDimensionPixelSize(
                R.dimen.bar_active_item_max_width
        );
        mActiveItemMinWidth=res.getDimensionPixelSize(
                R.dimen.bar_active_item_min_width
        );
        mBottomNavigationBarHeight=res.getDimensionPixelSize(
                R.dimen.bar_height);
        mInactiveItemMinWidth = res.getDimensionPixelSize(
                R.dimen.bar_inactive_item_min_width);
        mInactiveItemMaxWidth=res.getDimensionPixelSize(
                R.dimen.bar_inactive_item_max_width
        );

        mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationItem item = (BottomNavigationItem) v;
                //TODO :setFlag
                if(mListener==null||(mListener!=null&&mListener.onNavigationItemSelected(item,item.getPosition()))){
                    setItemSelected(item.getPosition());
                }
            }
        };

    }

    private void setItemSelected(int position) {
        if(mActivePosition==position)return;
        int shiftedColor = ((BottomNavigationItem) getChildAt(position)).getShiftedColor();
        if(shiftedColor!=0){
            ((BottomNavigationBar) getParent()).drawBackgroundCircle(shiftedColor,downX,downY);
        }
        mActivePosition=position;
        for(int i=0;i<getChildCount();i++)
        {
            final BottomNavigationItem item = (BottomNavigationItem) getChildAt(i);
            item.setSelected(i==position?true:false);
        }

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            downX=ev.getRawX();
            downY=ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float downX;
    private float downY;

    //onTouchEvent 除非被拦截或者子view不处理才会调用
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(event.getAction()==MotionEvent.ACTION_DOWN){
//            downX=event.getRawX();
//            downY=event.getRawY();
//            Log.i("test","downX:"+downX);
//            Log.i("test","downY:"+downY);
//        }
//        return super.onTouchEvent(event);
//    }


    public void setItems(List<BottomNavigationItem> bottomNavigationItems){
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,mBottomNavigationBarHeight));
        int counts =bottomNavigationItems.size();
        widthSpec = new int[counts];
        int screenWidth=BarUtils.getDeviceWidth(getContext());
//        int heightSpec=MeasureSpec.makeMeasureSpec(mBottomNavigationBarHeight,MeasureSpec.EXACTLY);

        int remain_activeMax;
        int activeItem;
        int inActiveItem;
        int remain;
        //shift mode
        if(mSwitchMode==1){
             remain_activeMax=screenWidth-(counts-1)*mInactiveItemMinWidth;
             activeItem=Math.min(mActiveItemMaxWidth,remain_activeMax);

            if(activeItem<mActiveItemMinWidth)activeItem=mActiveItemMinWidth;
            int remain_inActiveMax=(screenWidth - activeItem) / (counts - 1);
            inActiveItem=Math.min(mInactiveItemMaxWidth,remain_inActiveMax);

            remain=screenWidth-activeItem-(counts-1)*inActiveItem;
        }
        else {
             remain_activeMax=screenWidth/counts;
             activeItem=Math.min(mActiveItemMaxWidth,remain_activeMax);
             inActiveItem=activeItem;
             remain=screenWidth-activeItem*counts;
        }

        for (int i=0;i<counts;i++)
        {
            widthSpec[i]=mActivePosition==i?activeItem:inActiveItem;
            if(remain>0){
                widthSpec[i]++;
                remain--;
            }
            final BottomNavigationItem item = bottomNavigationItems.get(i);
            item.setClickable(true);
            item.setPosition(i);
            item.setOnClickListener(mOnClickListener);
                item.setActiveItemWidth(activeItem);
                item.setInActiveItemWidth(inActiveItem);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                    widthSpec[i],mBottomNavigationBarHeight);
            item.setLayoutParams(layoutParams);
            addView(item);
//            if(i==mActivePosition)setItemSelected(mActivePosition);
        }
    }
    private BottomNavigationBar.OnNavigationItemSelectedListener mListener;
    public void injectListener(BottomNavigationBar.OnNavigationItemSelectedListener mListener) {
        this.mListener=mListener;
    }

    public void finishInit(List<BottomNavigationItem> bottomNavigationItems) {
        for(BottomNavigationItem item: bottomNavigationItems){
            item.finishInit();
        }
    }

    public BottomNavigationBarContent setSwitchMode(int mSwitchMode) {
        this.mSwitchMode = mSwitchMode;
        return this;
    }
}
