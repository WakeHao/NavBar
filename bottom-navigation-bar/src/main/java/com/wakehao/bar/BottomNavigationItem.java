package com.wakehao.bar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class BottomNavigationItem extends View {
    private @DrawableRes int iconRes;
    private String title;
    private Config config;
    private int mPosition;
    private @ColorInt int mShiftedColor;
    private boolean initFinished;
    private Paint mPaint;
    private final int mActiveMarginTop;
    private final int mScaleInactiveMarginTop;
    private final int mShiftInactiveMarginTop;
    private final int mActiveMarginBottom;
    private final int mIconSize;
    private final int mActiveTextSize;
    private final int mInactiveTextSize;

    private Bitmap mBitmap;
    private static final long ACTIVE_ANIMATION_DURATION_MS = 150L;
    private int activeItemWidth;
    private int inActiveItemWidth;

    public BottomNavigationItem(Context context) {
        this(context,null);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources res=getResources();
        mActiveMarginTop=res.getDimensionPixelSize(R.dimen.item_active_marginTop);
        mScaleInactiveMarginTop=res.getDimensionPixelSize(R.dimen.item_scaleInactive_marginTop);
        mShiftInactiveMarginTop=res.getDimensionPixelSize(R.dimen.item_shiftInactive_marginTop);
        mActiveMarginBottom=res.getDimensionPixelSize(R.dimen.item_active_marginBottom);
        mIconSize=res.getDimensionPixelSize(R.dimen.item_icon_size);
        mActiveTextSize=res.getDimensionPixelSize(R.dimen.item_active_text_size);
        mInactiveTextSize=res.getDimensionPixelSize(R.dimen.item_inactive_text_size);

    }

    private void initDefaultOption() {
        if(mPosition==0&&mShiftedColor!=0) ((BottomNavigationBar) getParent().getParent()).setFirstItemBackgroundColor(mShiftedColor);
        if(mShiftedColor==0)setItemBackground(config.itemBackGroundRes);//recall onDraw()
        mBitmap= BitmapFactory.decodeResource(getResources(),iconRes);
    }


    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Config getConfig() {
        return config;
    }

    public int getPosition() {
        return mPosition;
    }


    public void setPosition(int mPosition) {
        this.mPosition = mPosition;

    }

    public void setShiftedColor(int mShiftedColor) {
        this.mShiftedColor=mShiftedColor;

    }

    public int getShiftedColor() {
        return mShiftedColor;
    }

    public void setInActiveItemWidth(int inActiveItemWidth) {
        this.inActiveItemWidth = inActiveItemWidth;
    }

    public void setActiveItemWidth(int activeItemWidth) {
        this.activeItemWidth = activeItemWidth;
    }

    //标志item初始化完毕
    public void finishInit() {
        initFinished=true;
        initDefaultOption();
    }

    public static class Config
    {
        private int activeColor;
        private int inActiveColor;
        private int itemBackGroundRes;
        private int switchMode;

        public Config(Build build) {
            activeColor=build.activeColor;
            inActiveColor=build.inActiveColor;
            itemBackGroundRes=build.itemBackGroundRes;
            switchMode=build.switchMode;
        }

        public int getSwitchMode() {
            return switchMode;
        }

        public static class Build{
            private int activeColor;
            private int inActiveColor;
            private int itemBackGroundRes;
            private int switchMode;
            public Build setActiveColor(int activeColor) {
                this.activeColor = activeColor;
                return this;
            }

            public Build setInActiveColor(int inActiveColor) {
                this.inActiveColor = inActiveColor;
                return this;
            }

            public Build setItemBackGroundRes(int itemBackGroundRes) {
                this.itemBackGroundRes = itemBackGroundRes;
                return this;
            }

            public Build setSwitchMode(int switchMode) {
                this.switchMode = switchMode;
                return this;
            }


            public Config build()
            {
                return new Config(this);
            }

        }
    }

    public void setConfig(Config config)
    {
        this.config=config;
    }

    private boolean isSelected;
    public void setSelected(boolean isSelected){
        this.isSelected=isSelected;
        translateAnim();
    }

    public void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0
                ? null : ContextCompat.getDrawable(getContext(), background);
        ViewCompat.setBackground(this, backgroundDrawable);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(initFinished){
            initPaint();
            drawIcon(canvas);
            drawText(canvas);
        }
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    RectF rectF;
    Rect rect;
    boolean isRefresh;
    private void drawIcon(Canvas canvas) {
        if(isRefresh){
            canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }
        if(rect==null)rect=new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
        if(rectF==null){
            rectF=new RectF();
        }
        if(mPosition==0){
            rectF.set(getWidth()/2-mIconSize/2,mActiveMarginTop,getWidth()/2+mIconSize/2,mActiveMarginTop+mIconSize);
        }
        else {
            rectF.set(getWidth()/2-mIconSize/2,mShiftInactiveMarginTop,getWidth()/2+mIconSize/2,mShiftInactiveMarginTop+mIconSize);
        }
        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
    }

    private void drawText(Canvas canvas) {

    }

    private void changeBitmapColor(@ColorInt int color) {
        ColorFilter filter = new LightingColorFilter(color, 1);
        mPaint.setColorFilter(filter);
    }

    public void translateAnim(){
        ValueAnimator valueAnimator;
        if(isSelected){
            valueAnimator=ValueAnimator.ofFloat(inActiveItemWidth,activeItemWidth);
        }
        else {
            //宽度没改变的不执行动画
            if(inActiveItemWidth==getWidth()||Math.abs(getWidth()-inActiveItemWidth)<=1)return;
            valueAnimator=ValueAnimator.ofFloat(getWidth(),inActiveItemWidth);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                float change = animation.getAnimatedFraction()*(mShiftInactiveMarginTop-mActiveMarginTop);
                if(isSelected){
                    rectF.set(animatedValue/2-mIconSize/2,(mShiftInactiveMarginTop-change),animatedValue/2+mIconSize/2,(mShiftInactiveMarginTop-change)+mIconSize);
                }
                else {
                    rectF.set(animatedValue/2-mIconSize/2,(mActiveMarginTop+change),animatedValue/2+mIconSize/2,(mActiveMarginTop+change)+mIconSize);
                }

                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) return;

                params.width = Math.round((float) animation.getAnimatedValue());
                setLayoutParams(params);
                isRefresh=true;

                invalidate();

            }
        });
        valueAnimator.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                isRefresh=false;
            }
        });
        valueAnimator.start();
    }

}
