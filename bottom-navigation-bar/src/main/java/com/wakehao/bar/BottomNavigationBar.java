package com.wakehao.bar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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


    public BottomNavigationBar(Context context) {
        super(context,null);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


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



        itemBackGroundRes = typedArray.getResourceId(R.styleable.BottomNavigationBar_itemBackground, 0);

        if(typedArray.hasValue(R.styleable.BottomNavigationBar_menu)){
            ItemParser parser=new ItemParser(context,getDefaultConfig());
            parser.parser(typedArray.getResourceId(R.styleable.BottomNavigationBar_menu,0));
            mBottomNavigationBarContent=new BottomNavigationBarContent(context);
            mBottomNavigationBarContent.setItems(parser.getBottomNavigationItems());
            FrameLayout.LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            addView(mBottomNavigationBarContent,layoutParams);
            mBottomNavigationBarContent.finishInit(parser.getBottomNavigationItems());
        }
        else {

        }
        typedArray.recycle();
        if(Build.VERSION.SDK_INT<21){
            //TODO :addShade
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

    }

    public void setFirstItemBackgroundColor(int shiftedColor){
        setBackgroundColor(shiftedColor);
    }
    private BottomNavigationItem.Config getDefaultConfig() {
        return new BottomNavigationItem.Config.Build()
                //TODO setValue
                .setItemBackGroundRes(itemBackGroundRes)
                .setSwitchMode(mSwitchMode)
                .build();
    }

    @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec=MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(BarUtils.dip2px(getContext(),56),MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void drawBackgroundCircle(int shiftedColor,float x,float y) {
        if(mSwitchMode!=1)return;
        if(currentRadius==0f)currentRadius=BarUtils.dip2px(getContext(),2);
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
    private void drawHighVersionCircle(final int shiftedColor, int x, int y) {
        Animator animator = ViewAnimationUtils.createCircularReveal(
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
                setBackgroundColor(shiftedColor);
                circleView.setVisibility(View.INVISIBLE);
                ViewCompat.setAlpha(circleView, 1);
            }
        });
        animator.start();
    }

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
        invalidate();
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
                invalidate();
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


    public interface OnNavigationItemSelectedListener {

        boolean onNavigationItemSelected(@NonNull BottomNavigationItem item,int selectedPosition);
    }

    public void setOnNavigationItemSelectedListener(
            @Nullable OnNavigationItemSelectedListener listener) {
        mBottomNavigationBarContent.injectListener(listener);
    }

}
