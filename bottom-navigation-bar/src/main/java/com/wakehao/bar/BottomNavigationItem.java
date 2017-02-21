package com.wakehao.bar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class BottomNavigationItem extends View {
    private @DrawableRes int iconRes;
    private @DrawableRes int iconRes2_selected;
    private String title;
    private Config config;
    private int mPosition;
    private @ColorInt int mShiftedColor;
    private boolean initFinished;
    private Paint mPaint;
    public final int mActiveMarginTop;
    private final int mScaleInactiveMarginTop;
    public final int mShiftInactiveMarginTop;
    private final int mActiveMarginBottom;
    private final int mIconSize;
    private final int mActiveTextSize;
    private final int mInactiveTextSize;

    private Fragment mFragment;
    private Bitmap mBitmap;
    private static final long ACTIVE_ANIMATION_DURATION_MS = 150L;
    private int activeItemWidth;
    private int inActiveItemWidth;

    public float currentMarginTop;

    private boolean isViewPager;
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

    Bitmap bitmap_selected;
    private void initDefaultOption() {
        if(mPosition==0){
            isSelected=true;
            if(mFragment!=null&&!isViewPager){

                if(!mFragment.isAdded())getActivity().getSupportFragmentManager().beginTransaction()
                        .add(getContainerId(),mFragment,tag).commitAllowingStateLoss();
                else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .show(mFragment).commitAllowingStateLoss();
                }
            }
        }
        if(mPosition==0&&mShiftedColor!=0) ((BottomNavigationBar) getParent().getParent()).setFirstItemBackgroundColor(mShiftedColor);
        if(mShiftedColor==0)setItemBackground(config.itemBackGroundRes);//recall onDraw()
        mBitmap= BitmapFactory.decodeResource(getResources(),iconRes);
        initPaint();
        if(iconRes2_selected!=0){
            //change bitmap
            bitmap_selected=BitmapFactory.decodeResource(getResources(),iconRes2_selected);
            initSecondPaint();
            if(isSelected){
                mUnSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
            else {
                mSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
        }
//        changeUnSelectedIconColorFilter(config.inActiveColor);
        init();
        if(config.isSlide&&iconRes2_selected==0){
            throw new RuntimeException("you need provide 2 pictures in Slide mode at least");
        }

    }
    
    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public void setIconResSelected(int iconRes2_selected){
        this.iconRes2_selected=iconRes2_selected;
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


    /*
    0-->1 activeColor-->inActiveColor
    1-->0 inActiveColor-->activeColor
     */
    public void textAlphaAnim(float positionOffset){
        mPaint.setColor(BarUtils.getOffsetColor(positionOffset,config.activeColor,config.inActiveColor,10));
//        invalidate();
    }

    private void iconAlphaAnim(float positionOffset){
        changeUnSelectedIconColorFilter(BarUtils.getIconColor(positionOffset, Color.TRANSPARENT, config.activeColor,config.inActiveColor, 10));
        changeSelectedIconColorFilter(BarUtils.getIconColor(positionOffset, config.activeColor, Color.TRANSPARENT, Color.TRANSPARENT, 10));
//        invalidate();
    }

    private boolean isSliding;
    public void alphaAnim(float positionOffset) {
//        if(hasCorrected)return;
//        if(config.switchMode!=1) {
//            if (Math.abs(positionOffset - this.positionOffset) <= 0.05) return;
//            this.positionOffset = positionOffset;
//        }
//        if((System.currentTimeMillis()-startTime)<100){
//
//            return;
//        }
        isSliding=true;
        if(!config.isSlide)return;
        //TODO 在点击的时候也需要将图片换底色
        iconAlphaAnim(positionOffset);
        textAlphaAnim(positionOffset);

        //scale mode scaled by positionOffset
        if(config.switchMode==0){
            //marginTop 6-->8区间渐变
            rectF.set(getWidth()/2-mIconSizeWidth/2,getScaledY(positionOffset),getWidth()/2+mIconSizeWidth/2,getScaledY(positionOffset)+mIconSizeHeight);
            updateTextPaint(getScaledSp(positionOffset));
            correctDotViewPosition((int) getScaledY(positionOffset));
        }
        else if(config.switchMode==1){
            //width activeItemWidth-->InActiveItemWidth区间渐变

            rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop+(mShiftInactiveMarginTop-mActiveMarginTop)*positionOffset,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+(mShiftInactiveMarginTop-mActiveMarginTop)*positionOffset+mIconSizeHeight);
            //14sp->0
            updateTextPaint(mActiveTextSize *(1-positionOffset));
            ((BottomNavigationItemWithDot) getParent()).setDotTop((int) (mActiveMarginTop+(mShiftInactiveMarginTop-mActiveMarginTop)*positionOffset));
            updateItemWidth(activeItemWidth-(activeItemWidth-inActiveItemWidth)*positionOffset);
//            correctDotViewPosition((int) (mActiveMarginTop+(mShiftInactiveMarginTop-mActiveMarginTop)*positionOffset));
        }
        //#onPageSelected() has been called when positionOffset was about 0.7&0.3
        if(!isSelected&&positionOffset>=0.99){
            correctItemData(false);
            return;
        }
        else if(isSelected&&positionOffset<=0.01){
            correctItemData(true);
            return;
        }
        invalidate();
    }

   //TODO add 校正判断
    private boolean isCallCorrect() {

        return false;
    }

    public void correctItemData(boolean isSelected){
        this.isSelected=isSelected;
        if(isSelected){
            if(config.switchMode==2){
                changeColor(config.activeColor);
                changeSelectedIconColorFilter(config.activeColor);
                mUnSelectedIconPaint.setColor(Color.TRANSPARENT);
                return;
            }
            if(config.switchMode==1){
                updateItemWidth(activeItemWidth);
                updateTextPaint(mActiveTextSize);
                rectF.set(activeItemWidth/2-mIconSizeWidth/2,mActiveMarginTop,activeItemWidth/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);
            }
            else {
                updateTextPaint(mActiveTextSize);
                rectF.set(activeItemWidth/2-mIconSizeWidth/2,mActiveMarginTop,activeItemWidth/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);
            }
        }
        else {
            if(config.switchMode==2){
                changeColor(config.inActiveColor);
                changeUnSelectedIconColorFilter(config.inActiveColor);
                mSelectedIconPaint.setColor(Color.TRANSPARENT);
                return;
            }
            if(config.switchMode==1){
                updateItemWidth(inActiveItemWidth);
                updateTextPaint(0);
                rectF.set(inActiveItemWidth/2-mIconSizeWidth/2,mShiftInactiveMarginTop,mIconSizeWidth/2+inActiveItemWidth/2,mShiftInactiveMarginTop+mIconSizeHeight);
            }
            else {
                updateTextPaint(mInactiveTextSize);
                rectF.set(inActiveItemWidth/2-mIconSizeWidth/2,mScaleInactiveMarginTop,inActiveItemWidth/2+mIconSizeWidth/2,mScaleInactiveMarginTop+mIconSizeHeight);
            }
        }
        setSelected(isSelected,false);
    }


    /**
     * 校正小红点的位置
     * @param top
     */
    public void correctDotViewPosition(int top) {
        ((BottomNavigationItemWithDot) getParent()).correctDotViewPosition(top);
    }


    //6dp-->8dp
    private float  getScaledY(float offset){
        return  (mActiveMarginTop+(mScaleInactiveMarginTop-mActiveMarginTop)*offset);
    }

    //14sp->12sp
    private float getScaledSp(float offset){
        return mActiveTextSize-(mActiveTextSize-mInactiveTextSize)*offset;
    }

    //selected bitmap
    private Paint mSelectedIconPaint;
    //unSelected bitmap
    private Paint mUnSelectedIconPaint;
    private void initSecondPaint() {
        if(mSelectedIconPaint==null){
            mSelectedIconPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

            mSelectedIconPaint.setFilterBitmap(true);
            mUnSelectedIconPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
            mUnSelectedIconPaint.setFilterBitmap(true);
        }
    }

    public static class Config
    {
        private int activeColor;
        private int inActiveColor;
        private int itemBackGroundRes;
        private int switchMode;
        private boolean isSlide;

        public Config(Build build) {
            activeColor=build.activeColor;
            inActiveColor=build.inActiveColor;
            itemBackGroundRes=build.itemBackGroundRes;
            switchMode=build.switchMode;
            isSlide=build.isSlide;
        }

        public int getSwitchMode() {
            return switchMode;
        }

        public static class Build{
            private int activeColor;
            private int inActiveColor;
            private int itemBackGroundRes;
            private int switchMode;
            private boolean isSlide;
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

            public Build setIsSlide(boolean isSlide) {
                this.isSlide = isSlide;
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

//    public void setSelected(boolean isSelected,boolean isAnim){
//        this.isSelected=isSelected;
//        if(isSelected)((BottomNavigationBarContent) getParent().getParent()).updatePosition(mPosition);
//        changeColor(isSelected?config.activeColor:config.inActiveColor);
//        if(config.isSlide){
//            if(isSelected){
//                changeSelectedIconColorFilter(config.activeColor);
//                mUnSelectedIconPaint.setColor(Color.TRANSPARENT);
//            }
//            else {
//                changeUnSelectedIconColorFilter(config.inActiveColor);
//                mSelectedIconPaint.setColor(Color.TRANSPARENT);
//            }
//        }
//        updateView();
//    }

    private void updateView(){
        if(Looper.myLooper()==Looper.getMainLooper()){
            invalidate();
        }
        else {
            postInvalidate();
        }
    }

    private boolean isSelected;
    public void setSelected(boolean isSelected,boolean isAnim){
        this.isSelected=isSelected;
        changeColor(isSelected?config.activeColor:config.inActiveColor);
        if(config.isSlide){
            if(isSelected){

                changeSelectedIconColorFilter(config.activeColor);
                mUnSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
            else {
                changeUnSelectedIconColorFilter(config.inActiveColor);
                mSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
        }
        if(!isAnim){
            updateView();
            return;
        }
        switch (config.getSwitchMode()){
            case 0:
                scaleAnim();
                break;
            case 1:
                translateAnim();
                break;
            case 2:
                invalidate();
                break;
        }

        if(!isViewPager)switchFragment(isSelected);
    }

    private void switchFragment(boolean isSelected) {
        if(mFragment==null)return;
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
//        ((AppCompatActivity) getContext()).get

        if(!isSelected){
            if(mFragment.isAdded())fragmentTransaction.hide(mFragment);
        }
        else {
            if(mFragment.isAdded())fragmentTransaction.show(mFragment);
            else fragmentTransaction.add(getContainerId(),mFragment,tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private @IdRes int containerId;
    private @IdRes int  getContainerId(){
        if(containerId!=0)return containerId;
        return containerId=((BottomNavigationBar) getParent().getParent().getParent()).getContainerId();
    }

    private float scaleFraction;
    private void scaleAnim() {
        isSliding=false;
        final ValueAnimator scaleAnimator;
        if(isSelected){
            scaleAnimator=ValueAnimator.ofFloat(mScaleInactiveMarginTop,mActiveMarginTop);
        }
        else {
//            if(mPaint.getTextSize()==mInactiveTextSize)return;
            if(Math.abs(mPaint.getTextSize()-mInactiveTextSize)<1)return;
            scaleAnimator=ValueAnimator.ofFloat(mActiveMarginTop,mScaleInactiveMarginTop);
        }
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isRefresh=true;
                scaleFraction=animation.getAnimatedFraction();
                float change=scaleFraction*(mScaleInactiveMarginTop-mActiveMarginTop);
                if(isSelected){
                    currentMarginTop=mScaleInactiveMarginTop-change;
                    rectF.set(getWidth()/2-mIconSizeWidth/2,currentMarginTop,getWidth()/2+mIconSizeWidth/2,currentMarginTop+mIconSizeHeight);
                }
                else {
                    currentMarginTop=mActiveMarginTop+change;
                    rectF.set(getWidth()/2-mIconSizeWidth/2,currentMarginTop,getWidth()/2+mIconSizeWidth/2,currentMarginTop+mIconSizeHeight);
                }
                ((BottomNavigationItemWithDot) getParent()).correctDotViewPosition((int) currentMarginTop);
                invalidate();
            }
        });
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                isRefresh=false;
            }
        });
        scaleAnimator.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        scaleAnimator.start();
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
            switch (config.getSwitchMode()){
                case 0:
                    drawScaledIcon(canvas);
                    drawScaledText(canvas);
                    break;
                case 1:
                    drawShiftedIcon(canvas);
                    drawShiftedText(canvas);
                    break;
                case 2:
                    drawStillIcon(canvas);
                    drawStillText(canvas);
                    break;
            }
        }

    }

    private void drawStillText(Canvas canvas) {
        updateTextPaint(mActiveTextSize);
        canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
    }

    private void drawStillIcon(Canvas canvas) {
        rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);
        if(config.isSlide&&iconRes2_selected!=0){
            canvas.drawBitmap(mBitmap, rect, rectF, mUnSelectedIconPaint);
            canvas.drawBitmap(bitmap_selected, rect, rectF, mSelectedIconPaint);
            return;
        }
        if(iconRes2_selected!=0){
            if(isSelected) canvas.drawBitmap(bitmap_selected, rect, rectF, mPaint);
            else  canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }
        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);

    }

    private void init() {
        if (textRect == null) {
            textRect = new Rect();
        }

        if(rect==null){
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            //校正图片不是正方形变形问题
            if(width>height){
                mIconSizeWidth=mIconSize;
                mIconSizeHeight=(((float)height/(float)width))*mIconSize;
            }
            else if(width<height)
            {
                mIconSizeHeight=mIconSize;
                mIconSizeWidth=(width/height)*mIconSize;
            }
            else {
                mIconSizeWidth=mIconSize;
                mIconSizeHeight=mIconSize;
            }
            rect=new Rect(0,0,width,height);

        }
        if(rectF==null){
            rectF=new RectF();
        }

        if(mPosition==0)changeColor(config.activeColor);
        else changeColor(config.inActiveColor);

    }

    private void updateTextPaint(float textSize){
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(title, 0, title.length(), textRect);
    }
    private void drawScaledText(Canvas canvas) {

        if(config.isSlide&&isSliding){
            canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
            return;
        }

        if(isRefresh){
            if(isSelected){
                updateTextPaint(mInactiveTextSize+(mActiveTextSize-mInactiveTextSize)*scaleFraction);
            }
            else {

                updateTextPaint(mActiveTextSize-(mActiveTextSize-mInactiveTextSize)*scaleFraction);
            }
            canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
            return;
        }
        updateTextPaint(mPosition==0?mActiveTextSize:mInactiveTextSize);

        canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
    }

    private float mIconSizeWidth;
    private float mIconSizeHeight;



    private void drawScaledIcon(Canvas canvas) {
        //点击scaleAnim动画
        if(isRefresh){
            if(iconRes2_selected!=0){
//                canvas.drawBitmap(isSelected?bitmap_selected:mBitmap, rect, rectF, mPaint);
                canvas.drawBitmap(mBitmap, rect, rectF, mUnSelectedIconPaint);
                canvas.drawBitmap(bitmap_selected, rect, rectF, mSelectedIconPaint);
                return;
            }
            canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }

        //第一次设置初始rectF的值
        if(rectF.isEmpty()){

            if(isSelected){
                currentMarginTop=mActiveMarginTop;

                rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);
            }
            else {
                currentMarginTop=mScaleInactiveMarginTop;
                rectF.set(getWidth()/2-mIconSizeWidth/2,mScaleInactiveMarginTop,getWidth()/2+mIconSizeWidth/2,mScaleInactiveMarginTop+mIconSizeHeight);
            }
        }
        //页面偏移
        if(config.isSlide&&iconRes2_selected!=0){
            canvas.drawBitmap(mBitmap, rect, rectF, mUnSelectedIconPaint);
            canvas.drawBitmap(bitmap_selected, rect, rectF, mSelectedIconPaint);
            return;
        }
        if(iconRes2_selected!=0){
            if(isSelected) canvas.drawBitmap(bitmap_selected, rect, rectF, mPaint);
            else  canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }
        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);

    }

    private void drawShiftedText(Canvas canvas) {
        if (isRefresh) {
            if (isSelected) {
//                mPaint.setTextSize(mActiveTextSize * animatedFraction);
                updateTextPaint(mActiveTextSize * animatedFraction);
            } else {
                // TODO FIXERROR
//                mPaint.setTextSize(mActiveTextSize - mActiveTextSize * animatedFraction);
                updateTextPaint(mActiveTextSize - mActiveTextSize * animatedFraction);
            }
            canvas.drawText(title, getWidth() / 2 - textRect.width() / 2, BarUtils.dip2px(getContext(), 46), mPaint);
            return;
        }
        if(mPosition!=0&&textRect.isEmpty()){

            return;
        }
        if(textRect.isEmpty())mPaint.getTextBounds(title, 0, title.length(), textRect);
        canvas.drawText(title, getWidth() / 2 - textRect.width() / 2, BarUtils.dip2px(getContext(), 46), mPaint);
//        if (mPosition == 0) {
//            canvas.drawText(title, getWidth() / 2 - textRect.width() / 2, BarUtils.dip2px(getContext(), 46), mPaint);
//        }
//        if(config.isSlide&&isSliding){
//            canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
////            return
//        }


    }


    private void drawShiftedIcon(Canvas canvas) {

        //点击shiftAnim动画
        if(isRefresh){
            if(iconRes2_selected!=0){
//                canvas.drawBitmap(isSelected?bitmap_selected:mBitmap, rect, rectF, mPaint);
                canvas.drawBitmap(mBitmap, rect, rectF, mUnSelectedIconPaint);
                canvas.drawBitmap(bitmap_selected, rect, rectF, mSelectedIconPaint);
                return;
            }
            canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }

        //第一次设置初始rectF的值
        if(rectF.isEmpty()) {
            if (isSelected) {
                rectF.set(getWidth() / 2 - mIconSizeWidth / 2, mActiveMarginTop, getWidth() / 2 + mIconSizeWidth / 2, mActiveMarginTop + mIconSizeHeight);
            } else {
                rectF.set(getWidth() / 2 - mIconSizeWidth / 2, mShiftInactiveMarginTop, getWidth() / 2 + mIconSizeWidth / 2, mShiftInactiveMarginTop + mIconSizeHeight);
            }
        }

        //页面偏移
        if(config.isSlide&&iconRes2_selected!=0){
            canvas.drawBitmap(mBitmap, rect, rectF, mUnSelectedIconPaint);
            canvas.drawBitmap(bitmap_selected, rect, rectF, mSelectedIconPaint);
            return;
        }
        if(iconRes2_selected!=0){
            if(isSelected) canvas.drawBitmap(bitmap_selected, rect, rectF, mPaint);
            else  canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }
        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);

    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setTextSize(mPosition==0?mActiveTextSize:mInactiveTextSize);

    }
    RectF rectF;
    Rect rect;
    boolean isRefresh;
    Rect textRect;
    float animatedFraction;


    private void changeColor(@ColorInt int color) {

        if(iconRes2_selected!=0){
            mPaint.setColor(color);
            return;
        }
        //TODO FIX

        ColorFilter filter = new LightingColorFilter(color, 1);
        mPaint.setColorFilter(filter);
        mPaint.setColor(color);
    }
//    ColorFilter filter;
    private void changeUnSelectedIconColorFilter(@ColorInt int color){
//        ColorFilter filter = new LightingColorFilter(color, 1);
//        mUnSelectedIconPaint.setColorFilter(filter);
        mUnSelectedIconPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mUnSelectedIconPaint.setColor(color);
    }
    private void changeSelectedIconColorFilter(int color){

//        ColorFilter filter = new LightingColorFilter(color, 1);
//        mSelectedIconPaint.setColorFilter(filter);
        mSelectedIconPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mSelectedIconPaint.setColor(color);
    }
    public void translateAnim(){
        isSliding=false;
        ValueAnimator valueAnimator;
        if(isSelected){
            valueAnimator=ValueAnimator.ofFloat(inActiveItemWidth,activeItemWidth);
        }
        else {
            //宽度没改变的不执行动画 TODO 10这个数值大小可能会有所变更
            if(inActiveItemWidth==getWidth()||Math.abs(getWidth()-inActiveItemWidth)<=10){
                return;
            }
            valueAnimator=ValueAnimator.ofFloat(getWidth(),inActiveItemWidth);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                animatedFraction = animation.getAnimatedFraction();
                float change = animatedFraction*(mShiftInactiveMarginTop-mActiveMarginTop);

                if(isSelected){
                    rectF.set(animatedValue/2-mIconSizeWidth/2,(mShiftInactiveMarginTop-change),animatedValue/2+mIconSizeWidth/2,(mShiftInactiveMarginTop-change)+mIconSizeHeight);
                        //not work
//                    mPaint.setTextSize(mActiveTextSize*animatedFraction);
                    ((BottomNavigationItemWithDot) getParent()).setDotTop((int) (mShiftInactiveMarginTop-change));
                }
                else {
                    rectF.set(animatedValue/2-mIconSizeWidth/2,(mActiveMarginTop+change),animatedValue/2+mIconSizeWidth/2,(mActiveMarginTop+change)+mIconSizeHeight);
//                    mPaint.setTextSize(mActiveTextSize-mActiveTextSize*animatedFraction);
                    ((BottomNavigationItemWithDot) getParent()).setDotTop((int)(mActiveMarginTop+change));
                }


                isRefresh=true;

                updateItemWidth((float) animation.getAnimatedValue());

            }
        });
        valueAnimator.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isRefresh=false;
            }
        });
        valueAnimator.start();
    }

    private void updateItemWidth(float currentWidth){
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) return;
        params.width = Math.round(currentWidth);
        setLayoutParams(params);
        invalidate();
    }

    private AppCompatActivity getActivity(){
        return ((AppCompatActivity) getContext());
    }

    public Fragment getFragment(){
        if(mFragment==null){
            //TODO FIX IT

            return null;
        }

        else return mFragment;
    }

    private String tag;

    public void setFragment(String fragmentPackageName){
        if(TextUtils.isEmpty(tag))tag=fragmentPackageName;
        mFragment = getActivity().getSupportFragmentManager().findFragmentByTag(fragmentPackageName);
        if(mFragment!=null){
            getActivity().getSupportFragmentManager().beginTransaction().hide(mFragment).commitAllowingStateLoss();
        }
        else {
            try {
                Class<?> aClass = Class.forName(fragmentPackageName);
                mFragment= (Fragment) aClass.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("you may provide a wrong packageName");
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isResume;
    public void setIsViewPager(boolean isViewPager){
        this.isViewPager=isViewPager;
    }

        @Override
        protected void onVisibilityChanged(View changedView, int visibility) {
            super.onVisibilityChanged(changedView, visibility);
            if(visibility==VISIBLE&&config.isSlide){
                correctItemData(isSelected);
            }
        }



}
