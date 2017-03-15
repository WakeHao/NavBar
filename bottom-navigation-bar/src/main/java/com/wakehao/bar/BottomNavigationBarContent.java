package com.wakehao.bar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
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

 class BottomNavigationBarContent extends LinearLayout {

    private final int mBottomNavigationBarHeight;

    private final int mActiveItemMaxWidth;
    private final int mActiveItemMinWidth;

    private final int mInactiveItemMaxWidth;
    private final int mInactiveItemMinWidth;


    private int mActivePosition=0;
    private OnClickListener mOnClickListener;
    private int[] widthSpec;
    private int mSwitchMode;
    private int counts;


     BottomNavigationBarContent(Context context) {
        this(context,null);
    }


    private BottomNavigationBarContent(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    private long LIMIT_OF_CLICK;
    private BottomNavigationBarContent(Context context, AttributeSet attrs, final int defStyleAttr) {
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
//                boolean flag=true;
                BottomNavigationItemWithDot item = (BottomNavigationItemWithDot) v;
                if(mListener==null|| (mListener!=null&&mListener.onNavigationItemSelected(item,item.getPosition()))){
                    if(System.currentTimeMillis()-LIMIT_OF_CLICK>=150L) {
                        //when is sliding,it can not been clicked
                        if(viewPager==null){
                            setItemSelected(item.getPosition(),true,true);
                        }
                        else {
                            if(((BottomNavigationBar) getParent()).getCanClick()){
                                viewPager.setCurrentItem(item.getPosition(), false);
                                setItemSelected(item.getPosition(),true,true);
                            }
                            LIMIT_OF_CLICK=System.currentTimeMillis();
                        }
                    }
                }
            }
        };
        setId(R.id.bar_content_private);

    }



    private boolean canChangeBackColor;

     boolean isCanChangeBackColor() {
        return canChangeBackColor;
    }

     void setCanChangeBackColor(boolean canChangeBackColor) {
        this.canChangeBackColor = canChangeBackColor;
    }

    /**
     * 选择item
     * @param position 选择的位置
     * @param isAnim 是否开启动画
     * @param isCanBackWave 是否容许开启点击水纹效果，在非slide且滑动结束后调用时才为false，其余为true
     */

     void setItemSelected(int position, boolean isAnim,boolean isCanBackWave) {

        if(mActivePosition==position)
        {
            if(mListener!=null)mListener.onNavigationItemSelectedAgain(getBottomItem(position),position);
            return;
        }

        int shiftedColor = ( ((BottomNavigationItemWithDot) getChildAt(position))).getShiftedColor();
         //只有每个item都设置了shfitColor才会由背景点击切换
         if(canChangeBackColor&&isCanBackWave){

             ((BottomNavigationBar) getParent()).drawBackgroundCircle(shiftedColor,downX,downY);

         }

         if(!isAnim){
             getBottomItem(position).correctItem(true);
             getBottomItem(mActivePosition).correctItem(false);
             mActivePosition=position;
             return;
         }


            getBottomItem(position).setSelected(true,isAnim);
            getBottomItem(mActivePosition).setSelected(false,isAnim);
            mActivePosition=position;
            return;



//        mActivePosition=position;
//        for(int i=0;i<getChildCount();i++)
//        {
//            final BottomNavigationItemWithDot item = ((BottomNavigationItemWithDot) getChildAt(i));
//            item.setSelected(i==position,isAnim);
//        }
    }

    void updatePosition(int mActivePosition){

        this.mActivePosition=mActivePosition;
    }
    int getActivePosition(){
        return mActivePosition;
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
//        }
//        return super.onTouchEvent(event);
//    }


     void setItems(List<BottomNavigationItemWithDot> bottomNavigationItems){
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,mBottomNavigationBarHeight));
        counts = bottomNavigationItems.size();
        widthSpec = new int[counts];
        int screenWidth=BarUtils.getDeviceWidth(getContext());
//        int heightSpec=MeasureSpec.makeMeasureSpec(mBottomNavigationBarHeight,MeasureSpec.EXACTLY);

        int remain_activeMax;
        int activeItem;
        int inActiveItem;
        int remain;
        //shift mode
        if(mSwitchMode==1){
             remain_activeMax=screenWidth-(counts -1)*mInactiveItemMinWidth;
             activeItem=Math.min(mActiveItemMaxWidth,remain_activeMax);

            if(activeItem<mActiveItemMinWidth)activeItem=mActiveItemMinWidth;
            int remain_inActiveMax=(screenWidth - activeItem) / (counts - 1);
            inActiveItem=Math.min(mInactiveItemMaxWidth,remain_inActiveMax);

            remain=screenWidth-activeItem-(counts -1)*inActiveItem;
        }
        else {
             remain_activeMax=screenWidth/ counts;
             activeItem=Math.min(mActiveItemMaxWidth,remain_activeMax);
             inActiveItem=activeItem;
             remain=screenWidth-activeItem* counts;
        }

        for (int i = 0; i< counts; i++)
        {
            widthSpec[i]=mActivePosition==i?activeItem:inActiveItem;
            if(remain>0){
                widthSpec[i]++;
                remain--;
            }
            final BottomNavigationItemWithDot item = bottomNavigationItems.get(i);
            item.setClickable(true);
            item.setPosition(i);
            item.setOnClickListener(mOnClickListener);
                item.setActiveItemWidth(activeItem);
                item.setInActiveItemWidth(inActiveItem);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                    widthSpec[i],mBottomNavigationBarHeight);
            item.setLayoutParams(layoutParams);
            addView(item);
//            if(i==mActivePosition)


        }
//        setItemSelected(0,true);
    }
    private ViewPager viewPager;

     void setViewPager(ViewPager viewPager){
        this.viewPager=viewPager;
    }
    private BottomNavigationBar.OnNavigationItemSelectedListener mListener;
     void injectListener(BottomNavigationBar.OnNavigationItemSelectedListener mListener) {
        this.mListener=mListener;
    }

     void finishInit(List<BottomNavigationItemWithDot> bottomNavigationItems, boolean isViewpager, boolean isSlide, boolean canChangeBackColor) {
        for(BottomNavigationItemWithDot item: bottomNavigationItems){
            item.setSlide(isSlide);
            item.setIsViewPager(isViewpager);
            item.setCanChangeBackColor(canChangeBackColor);
            item.finishInit();
        }
    }

     BottomNavigationBarContent setSwitchMode(int mSwitchMode) {
        this.mSwitchMode = mSwitchMode;
        return this;
    }

     void startAlphaAnim(int position, float positionOffset, boolean isMoving) {
       if(isMoving){
           getBottomItem(position).setHasCorrect(false).alphaAnim(positionOffset);
           getBottomItem(position+1).setHasCorrect(false).alphaAnim(1-positionOffset);
       }
        else{
           getBottomItem(position).alphaAnim(positionOffset);
           getBottomItem(position+1).alphaAnim(1-positionOffset);
       }
    }

     BottomNavigationItemWithDot getBottomItem(int position){
        return (((BottomNavigationItemWithDot) getChildAt(position)));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putInt("mActivePosition",mActivePosition);
        bundle.putParcelable("superState",super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof  Bundle){
            Bundle bundle= (Bundle) state;
            int mRestoreActivePosition = bundle.getInt("mActivePosition");
            setItemSelected(mRestoreActivePosition,true,true);
            state=bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}
