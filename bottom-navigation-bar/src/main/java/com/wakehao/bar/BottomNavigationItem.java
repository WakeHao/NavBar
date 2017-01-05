package com.wakehao.bar;

import android.support.annotation.DrawableRes;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class BottomNavigationItem  {
    private @DrawableRes int iconRes;
    private String title;

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
}
