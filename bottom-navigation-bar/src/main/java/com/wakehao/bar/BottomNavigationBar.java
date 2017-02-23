package com.wakehao.bar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.wakehao.bar.dot.DotView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class BottomNavigationBar extends FrameLayout{


    private BottomNavigationBarContent mBottomNavigationBarContent;
    private int itemBackGroundRes;
    private int mSwitchMode;
    private float maxRadius;
    private float currentRadius=0f;
    private Paint mPaint;
    private View circleView;
    private int activeColor;
    private int inActiveColor;
    private boolean isSlide;
//    private boolean isShy;
    private @IdRes int containerId;
    private int itemCounts;
    private int viewpagerId;
    private ViewPager viewpager;


    public BottomNavigationBar(Context context) {
        super(context,null);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(Build.VERSION.SDK_INT<21){

            View shadowView=new View(context);
            shadowView.setBackgroundResource(R.drawable.shadow);
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, BarUtils.dip2px(context,1));
            addView(shadowView,layoutParams);
        }
        else {
            circleView = new View(context);
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            addView(circleView,layoutParams);
        }

        TypedArray typedArray=context.obtainStyledAttributes(
                attrs,R.styleable.BottomNavigationBar,defStyleAttr,R.style.BottomNavigationView);


        if(typedArray.hasValue(R.styleable.BottomNavigationBar_switchMode)){
            mSwitchMode=typedArray.getInt(R.styleable.BottomNavigationBar_switchMode,0);
        }

        if(typedArray.hasValue(R.styleable.BottomNavigationBar_barElevation)){
            setBackgroundColor(Color.WHITE);
            ViewCompat.setElevation(
                    this,typedArray.getDimensionPixelSize(R.styleable.BottomNavigationBar_barElevation,0));
        }

        if(typedArray.hasValue(R.styleable.BottomNavigationBar_fragmentContainerId)){
            containerId=typedArray.getResourceId(R.styleable.BottomNavigationBar_fragmentContainerId,0);
        }

        if(typedArray.hasValue(R.styleable.BottomNavigationBar_viewpagerId)){
            viewpagerId = typedArray.getResourceId(R.styleable.BottomNavigationBar_viewpagerId,0);
            isSlide=(viewpagerId!=0);
//            if(viewpagerId!=0)getBottomItem()
        }

        itemBackGroundRes = typedArray.getResourceId(R.styleable.BottomNavigationBar_itemBackground, 0);
        activeColor = typedArray.getColor(R.styleable.BottomNavigationBar_selectedColor, BarUtils.getAppColorPrimary(context));
        inActiveColor = typedArray.getColor(R.styleable.BottomNavigationBar_unSelectedColor, Color.GRAY);
//        isSlide=typedArray.getBoolean(R.styleable.BottomNavigationBar_isSlide,false);
//        isShy=typedArray.getBoolean(R.styleable.BottomNavigationBar_isShy,false);

        if(typedArray.hasValue(R.styleable.BottomNavigationBar_menu)){
            ItemParser parser=new ItemParser(context,getDefaultConfig());
            parser.parser(typedArray.getResourceId(R.styleable.BottomNavigationBar_menu,0));
            mBottomNavigationBarContent=new BottomNavigationBarContent(context);
            itemCounts=parser.getItemCounts();
            mBottomNavigationBarContent.setSwitchMode(mSwitchMode).setItems(parser.getBottomNavigationItems());
            FrameLayout.LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            addView(mBottomNavigationBarContent,layoutParams);
            mBottomNavigationBarContent.finishInit(parser.getBottomNavigationItems(),viewpagerId!=0);
        }
        else {

        }
        typedArray.recycle();


    }

    protected boolean isMoving;
    protected boolean isBackMoving;

     boolean getCanClick(){
        return !isMoving&&!isBackMoving;
    }
    private void initViewPager(@IdRes int viewpagerId) {
        viewpager = (ViewPager) ((Activity) getContext()).findViewById(viewpagerId);
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(positionOffset==0f){
                    //在某些性能低的机器上 可能positionOffset会从0.4->0调到0，导致界面错位。
                    if(!getBottomItem(position).isHasCorrect()){
                        for(int i=0;i<itemCounts;i++){
                            getBottomItem(i).correctItemData(i==position,true);
                        }
                    }
                }
                if(positionOffset>0){
//                    if(isMoving){
                        startAlphaAnim(position,positionOffset,isMoving);
//                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                switch (state){
                    case 0:
                        isMoving=false;
                        isBackMoving=false;
                        break;
                    case 1:
                        isMoving=true;
                        isBackMoving=false;
                        break;
                    case 2:
                        isMoving=false;
                        isBackMoving=true;
                        break;
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(isBackMoving){
                    setItemSelected(position,false);
                }
            }
        });

        ((BottomNavigationBarContent) getChildAt(1)).setViewPager(viewpager);
        PagerAdapter pagerAdapter=new PagerAdapter(((AppCompatActivity) getContext()).getSupportFragmentManager());
        viewpager.setAdapter(pagerAdapter);
    }

     void setFirstItemBackgroundColor(int shiftedColor){
        setBackgroundColor(shiftedColor);
    }
    private BottomNavigationItem.Config getDefaultConfig() {
        return new BottomNavigationItem.Config.Build()
                .setItemBackGroundRes(itemBackGroundRes)
                .setSwitchMode(mSwitchMode)
                .setActiveColor(activeColor)
                .setInActiveColor(inActiveColor)
                .setIsSlide(isSlide)
                .build();
    }

    @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec=MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(BarUtils.dip2px(getContext(),56),MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

     void drawBackgroundCircle(int shiftedColor,float x,float y) {
        if(mSwitchMode!=1)return;
        if(currentRadius==0f)currentRadius=BarUtils.dip2px(getContext(),10);
        if(maxRadius==0f)maxRadius = (float) Math.sqrt(getMeasuredHeight()*getMeasuredHeight()+getMeasuredWidth()*getMeasuredWidth());
        if(Build.VERSION.SDK_INT<21){
            drawLowVersionCircle(shiftedColor,x,y);
        }
        else {
            prepareForBackgroundColorAnimation(shiftedColor);
            drawHighVersionCircle(shiftedColor,(int)x,(int)y);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawHighVersionCircle(final int shiftedColor, final int x, final int y) {
        if(isInflated){
            final Animator animator = ViewAnimationUtils.createCircularReveal(
                    circleView,
                    x,
                    y,
                    currentRadius,
                    maxRadius
            );
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onEnd();
                }

                private void onEnd() {
                    animator.removeListener(this);
                    setBackgroundColor(shiftedColor);
                    circleView.setVisibility(View.INVISIBLE);
                    ViewCompat.setAlpha(circleView, 1);
                }
            });
            animator.start();
        }
        else circleView.post(new Runnable() {
            @Override
            public void run() {
                final Animator animator = ViewAnimationUtils.createCircularReveal(
                        circleView,
                        x,
                        y,
                        currentRadius,
                        maxRadius
                );
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        onEnd();
                    }

                    private void onEnd() {
                        animator.removeListener(this);
                        setBackgroundColor(shiftedColor);
                        circleView.setVisibility(View.INVISIBLE);
                        ViewCompat.setAlpha(circleView, 1);
                    }
                });
                animator.start();
            }
        });
    }
//    private void startCircle(final int x, final int y){
//
//    }

    private void prepareForBackgroundColorAnimation(int newColor) {
        circleView.clearAnimation();
        circleView.setBackgroundColor(newColor);
        circleView.setVisibility(View.VISIBLE);
    }
    private float downX;
    private float downY;
    private void drawLowVersionCircle(int shiftedColor,float x,float y) {
        downX=x;
        downY=y;
        this.shiftedColor=shiftedColor;
        isStart=true;
        refreshView();
    }

    private void refreshView() {
        if(Looper.getMainLooper()== Looper.myLooper())invalidate();
        else postInvalidate();
    }

    private int shiftedColor;
    private boolean isStart;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isStart){
            initPaint();
            mPaint.setColor(shiftedColor);
            if(currentRadius<maxRadius){
                currentRadius+=currentRadius+maxRadius/30;
                canvas.drawCircle(downX,downY,currentRadius,mPaint);
                refreshView();
            }
            else{
                isStart=false;
                currentRadius=0f;
                setBackgroundColor(shiftedColor);
                canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
            }
        }
    }

    private void initPaint() {
        if(mPaint==null){
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    private void startAlphaAnim(int position, float positionOffset, boolean isMoving) {
        ((BottomNavigationBarContent) getChildAt(1)).startAlphaAnim(position,positionOffset,isMoving);
    }



    private boolean isInflated;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                isInflated=true;
                if(viewpagerId!=0){
                    try {
                        initViewPager(viewpagerId);

                    }
                    catch (Exception e){
                        throw new RuntimeException("you need provide a fragment packageName in menu's xml");
                    }
                }
            }
        });
    }

    public void disMissNum(int position) {
        ((BottomNavigationItemWithDot) ((BottomNavigationBarContent) getChildAt(1)).getChildAt(position)).disMissMes();
    }


    public interface OnNavigationItemSelectedListener {

        boolean onNavigationItemSelected(@NonNull BottomNavigationItem item,int selectedPosition);

        void onNavigationItemSelectedAgain(@NonNull BottomNavigationItem item,int reSelectedPosition);
    }

    public void setOnNavigationItemSelectedListener(
            @Nullable OnNavigationItemSelectedListener listener) {

        mBottomNavigationBarContent.injectListener(listener);
    }


    /**
     * set buy user
     * @param position
     */
    public void setItemSelected(final int position){
        if(position<0||position>itemCounts-1){
            throw new RuntimeException("the range of position is 0-"+(itemCounts-1));
        }
        if(isInflated){
            setItemSelected(position,true);
            if(viewpager!=null){
                viewpager.setCurrentItem(position,false);
            }
        }
        else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    setItemSelected(position,true);
                    if(viewpager!=null){
                        viewpager.setCurrentItem(position,false);
                    }
                }
            });
        }

    }


    private void setItemSelected(int position,boolean isAnim){
        ((BottomNavigationBarContent) getChildAt(1)).setItemSelected(position,isAnim);
    }

    public void showNum(int position,int num) {
//        hasMesPoint = true;
        ((BottomNavigationItemWithDot) ((BottomNavigationBarContent) getChildAt(1)).getChildAt(position)).showNum(num);
    }

      @IdRes int getContainerId(){
        return containerId;
    }

    class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getBottomItem(position).getFragment();
        }

        @Override
        public int getCount() {
            return itemCounts;
        }

    }

    private BottomNavigationItem getBottomItem(int position){

        return ((BottomNavigationItem) ((BottomNavigationItemWithDot) ((BottomNavigationBarContent) getChildAt(1)).getChildAt(position)).getChildAt(0));
    }

    public Fragment getFragment(int position){
        return getBottomItem(position).getFragment();
    }

    public ViewPager getViewPager(){
        if(viewpagerId!=0&&viewpager!=null){
            return viewpager;
        }
        else return null;
    }
}
