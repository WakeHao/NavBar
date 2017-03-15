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
import android.support.v4.view.animation.FastOutSlowInInterpolator;
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

import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;


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
        isSlide=typedArray.getBoolean(R.styleable.BottomNavigationBar_isSlide,false);
//        isShy=typedArray.getBoolean(R.styleable.BottomNavigationBar_isShy,false);

        if(typedArray.hasValue(R.styleable.BottomNavigationBar_menu)){
            ItemParser parser=new ItemParser(context,getDefaultConfig());
            parser.parser(typedArray.getResourceId(R.styleable.BottomNavigationBar_menu,0));
            mBottomNavigationBarContent=new BottomNavigationBarContent(context);
            itemCounts=parser.getItemCounts();
            mBottomNavigationBarContent.setSwitchMode(mSwitchMode).setItems(parser.getBottomNavigationItems());
            FrameLayout.LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            addView(mBottomNavigationBarContent,layoutParams);

            for(int i=0;i<itemCounts;i++){
                canChangeBackColor=canChangeBackColor&&(getBottomItem(i).getShiftedColor()!=0);
            }
            mBottomNavigationBarContent.setCanChangeBackColor(canChangeBackColor);
            mBottomNavigationBarContent.finishInit(parser.getBottomNavigationItems(),viewpagerId!=0,isSlide,canChangeBackColor);
        }
        else {

        }
        typedArray.recycle();
    }
    private boolean canChangeBackColor=true;

    boolean isCanChangeBackColor(){
        return canChangeBackColor;
    }


    protected boolean isMoving;
    protected boolean isBackMoving;
    protected float offset;

     boolean getCanClick(){
        return !isMoving&&!isBackMoving&offset==0;
    }
    private void initViewPager(@IdRes int viewpagerId) {
        viewpager = (ViewPager) ((Activity) getContext()).findViewById(viewpagerId);
        viewpager.setEnabled(false);
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                offset=positionOffset;
                if(positionOffset==0f){
//                    if(from==-1)return;
//                    //滑向右侧
//                    if(from<position){
//                        if(!getBottomItem(position).isHasCorrect())getBottomItem(position).correctItem(true);
//                        if(!getBottomItem(position-1).isHasCorrect())getBottomItem(position-1).correctItem(false);
//                    }
//                    else{
//                        if(!getBottomItem(position).isHasCorrect())getBottomItem(position).correctItem(true);
//                        if(!getBottomItem(position+1).isHasCorrect())getBottomItem(position+1).correctItem(false);
//                    }
                    //item 3->1 快速滑动可能会导致3不检索 所以每个item都需要校正一次
                    if(isSlide){
                        for(int i=0;i<itemCounts;i++){
                            if(!getBottomItem(i).isHasCorrect())getBottomItem(i).correctItem(i==position);
                        }
                    }
                }
                if(positionOffset>0){

                    if(isSlide){
                        startAlphaAnim(position,positionOffset,isMoving);
                        if(canChangeBackColor)setBackgroundColor(BarUtils.getOffsetColor(positionOffset,getBottomItem(position).getShiftedColor(),getBottomItem(position+1).getShiftedColor(),10));
                    }
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
//                    setItemSelected(position,false);
                    //不在此选中item，只是更新activePosition
                    if(isSlide)getChildView().updatePosition(position);
                    else {
                        //非slide模式 表示滑动切换到item时才执行动画 这里不执行背景水滴效果
                        setItemSelected(position,true,false);
                        correctBackColor(position);
                    }
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
    private BottomNavigationItemWithDot.Config getDefaultConfig() {
        return new BottomNavigationItemWithDot.Config.Build()
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
//        if(mSwitchMode!=1)return;
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


    /**
     * 滑动渐变结束校正
     */
    public void correctBackColor() {

        setBackgroundColor(getBottomItem(getChildView().getActivePosition()).getShiftedColor());
    }


    /**
     * 设置背景色为item position的color
     * @param position
     */
    public void correctBackColor(int position) {

        if(canChangeBackColor)setBackgroundColor(getBottomItem(position).getShiftedColor());
    }
    public interface OnNavigationItemSelectedListener {

        boolean onNavigationItemSelected(@NonNull BottomNavigationItemWithDot item,int selectedPosition);

        void onNavigationItemSelectedAgain(@NonNull BottomNavigationItemWithDot item,int reSelectedPosition);
    }

    public void setOnNavigationItemSelectedListener(
            @Nullable OnNavigationItemSelectedListener listener) {


        mBottomNavigationBarContent.injectListener(listener);
    }




    /**
     * set buy user
     * @param position
     */
    public void setItemSelected(final int position, final boolean isAnim){
        if(position<0||position>itemCounts-1){
            throw new RuntimeException("the range of position is 0-"+(itemCounts-1));
        }
        if(isInflated){
            setItemSelected(position,isAnim,false);
            correctBackColor(position);
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
                    setItemSelected(position,isAnim,false);
                    correctBackColor(position);
                    if(viewpager!=null){
                        viewpager.setCurrentItem(position,false);
                    }
                }
            });
        }

    }


    private void setItemSelected(int position,boolean isAnim,boolean isCanBackWave){
        getChildView().setItemSelected(position,isAnim,isCanBackWave);
    }
    public void hideBar(){
        if(getVisibility()==INVISIBLE)return;
        TransitionManager.beginDelayedTransition(this,new Slide());
        setVisibility(INVISIBLE);
    }
    public void hideBar(int mode){
        if(getVisibility()==INVISIBLE)return;
        TransitionManager.beginDelayedTransition(this,new Fade().setDuration(700));
        setVisibility(INVISIBLE);
    }
    public void showBar(){
        if(getVisibility()==VISIBLE)return;
        TransitionManager.beginDelayedTransition(this,new Slide());
        setVisibility(VISIBLE);
    }

    public void showBar(int mode){
        if(getVisibility()==VISIBLE)return;
        TransitionManager.beginDelayedTransition(this,new Fade().setDuration(700));
        setVisibility(VISIBLE);
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

    private BottomNavigationItemWithDot getBottomItem(int position){

        return  ((BottomNavigationItemWithDot) ((BottomNavigationBarContent) getChildAt(1)).getChildAt(position));
    }

    private BottomNavigationBarContent getChildView(){
        return ((BottomNavigationBarContent) getChildAt(1));
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

    /**
     *  改变position位置的item标题
     * @param position
     * @param title
     */
    public void setTitle(final int position, final String title){

        if(isInflated){
            getBottomItem(position).changeTitle(title);
        }
        else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    getBottomItem(position).changeTitle(title);
                }
            });
        }
    }
}
