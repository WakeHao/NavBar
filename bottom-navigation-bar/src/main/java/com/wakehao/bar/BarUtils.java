package com.wakehao.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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



}
