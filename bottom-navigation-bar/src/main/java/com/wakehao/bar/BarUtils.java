package com.wakehao.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.util.TypedValue;

import org.jetbrains.annotations.NotNull;

/**
 * Created by WakeHao on 2017/1/10.
 */

public class BarUtils {
    /**
     * 根据手机的分辨率从dp的单位转成为px(像素)
     */
    public static int dip2px(Context context, float dpValue){

        final float scale=context.getResources().getDisplayMetrics().density;

        return (int)(dpValue*scale+0.5f);

    }

    /**
     * 根据手机的分辨率从px(像素)的单位转成为dp
     */
    public static int px2dip(Context context,float pxValue) {

        final float scale=context.getResources().getDisplayMetrics().density;

        return (int)(pxValue/scale+0.5f);

    }

    public static float px2sp(@NotNull Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale);
    }

    public static float sp2px(@NotNull Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale);
    }

    public static Drawable changeDrawableColor(int drawableRes, int colorRes, Context context) {
        //Convert drawable res to bitmap
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        final Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        final Paint p = new Paint();
        final Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        //Create new drawable based on bitmap
        final Drawable drawable = new BitmapDrawable(context.getResources(), resultBitmap);
        drawable.setColorFilter(new
                PorterDuffColorFilter(colorRes, PorterDuff.Mode.MULTIPLY));
        return drawable;
    }
    public static int getDeviceWidth(Context context)
    {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private static final int[] APPCOMPAT_CHECK_ATTRS = {
            android.support.v7.appcompat.R.attr.colorPrimary
    };

    static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }

    public static @ColorInt int  getAppColorPrimary(Context context) {
        checkAppCompatTheme(context);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static @ColorInt int getOffsetColor(float offset,@ColorInt int startColor,@ColorInt int endColor,int steps){
        try {

            if(offset<=0.04)return startColor;
            if(offset>=0.96)return endColor;
//           int startRed=Integer.parseInt(""+Color.red(startColor),16);
//           int startGreen=Integer.parseInt(""+Color.green(startColor),16);
//           int startBlue=Integer.parseInt(""+Color.blue(startColor),16);
//
//           int endRed=Integer.parseInt(""+Color.red(endColor),16);
//           int endGreen=Integer.parseInt(""+Color.green(endColor),16);
//           int endBlue=Integer.parseInt(""+Color.blue(endColor),16);
           int startRed=Color.red(startColor);
           int startGreen=Color.green(startColor);
           int startBlue=Color.blue(startColor);

           int endRed=Color.red(endColor);
           int endGreen=Color.green(endColor);
           int endBlue=Color.blue(endColor);

           int newRed= (int) (startRed+((endRed-startRed)/steps)*(offset*steps));
           int newGreen= (int) (startGreen+((endGreen-startGreen))/steps*(offset*steps));
           int newBlue=(int) (startBlue+((endBlue-startBlue)/steps)*(offset*steps));


//           return Color.rgb(getHexInt(newRed),getHexInt(newGreen),getHexInt(newBlue));
           return Color.rgb(newRed,newGreen,newBlue);

       }
       catch (Exception e){
           return 0;
       }
    }
    /**
     * 将10进制颜色值转换成16进制。
     */
    private static int  getHexInt(int value) {
        String hexString = Integer.toHexString(value);
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        return Integer.valueOf(hexString);
    }

    /**
     * 0-->0.5->1 startColor-->middleColor->endColor
     * 1->0.5->0 endColor->middleColor->startColor
     * @param positionOffset
     */
    public static @ColorInt int  getIconColor(float positionOffset,@ColorInt int startColor,@ColorInt int middleColor,@ColorInt int endColor,int step){

        if(startColor==Color.TRANSPARENT){
            if(positionOffset<0.5){
                if(middleColor==Color.TRANSPARENT)return middleColor;
                return Color.argb((int) (0xff*positionOffset*2),Color.red(middleColor),Color.green(middleColor),Color.blue(middleColor));
            }
            else if(positionOffset==0.5){
                return middleColor;
            }
            else {
                if(middleColor==Color.TRANSPARENT){
                    if(endColor==Color.TRANSPARENT){
                        return middleColor;
                    }
                    return Color.argb((int)(0xff-(2*0xff*positionOffset)),Color.red(endColor),Color.green(endColor),Color.blue(endColor));
                }
                else {
                    if(endColor==Color.TRANSPARENT){
                        return Color.argb((int) (0xff-(2*0xff*positionOffset)),Color.red(endColor),Color.green(endColor),Color.blue(endColor));
                    }
                    return BarUtils.getOffsetColor((float) ((positionOffset-0.5)*2),middleColor,endColor,step);
                }
            }
        }
        else if(middleColor==Color.TRANSPARENT){
//            if(positionOffset==0){
//                return
//            }
             if(positionOffset<0.5){
                 //255->0
                return Color.argb((int) (0xff-(2*0xff*positionOffset)),Color.red(startColor),Color.green(startColor),Color.blue(startColor));
            }
            else if(positionOffset==0.5){
                return middleColor;
            }
            else {
                if(endColor==Color.TRANSPARENT){
                    return Color.TRANSPARENT;
                }
                return Color.argb((int) (0xff-(2*0xff*positionOffset)),Color.red(endColor),Color.green(endColor),Color.blue(endColor));
            }
        }
        else if(endColor==Color.TRANSPARENT){
            if(positionOffset<0.5){
                return BarUtils.getOffsetColor(positionOffset*2,startColor,middleColor,step);
            }
            else if(positionOffset==0.5){
                return middleColor;
            }
            else {
                return Color.argb((int) (0xff-(2*0xff*positionOffset)),Color.red(middleColor),Color.green(middleColor),Color.blue(middleColor));
            }
        }
        else {
            if(positionOffset<0.5){
                return BarUtils.getOffsetColor(positionOffset*2,startColor,middleColor,step);
            }
            else if(positionOffset==0.5){
                return middleColor;
            }
            else {
                return BarUtils.getOffsetColor((float) ((positionOffset-0.5)*2),middleColor,endColor,step);
            }
        }
    }



}
