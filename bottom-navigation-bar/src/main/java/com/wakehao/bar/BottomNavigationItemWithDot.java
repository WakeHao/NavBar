package com.wakehao.bar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.wakehao.bar.dot.DotView;

/**
 * Created by WakeHao on 2017/2/6.
 */

public class BottomNavigationItemWithDot extends FrameLayout {
    private DotView mDotView;
    private int dotTop;
    private boolean hasMesPoint;

    private BottomNavigationItem mBottomNavigationItem;
    public BottomNavigationItemWithDot(Context context,BottomNavigationItem mBottomNavigationItem) {
        super(context);
        this.mBottomNavigationItem=mBottomNavigationItem;
        mDotView=new DotView(context);

        addView(mBottomNavigationItem);
        addView(mDotView);

        dotTop=mBottomNavigationItem.getPosition()==0?mBottomNavigationItem.mActiveMarginTop:mBottomNavigationItem.mShiftInactiveMarginTop;
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mBottomNavigationItem.getConfig().getSwitchMode()!=1) {
            dotTop=(int) mBottomNavigationItem.currentMarginTop;
        }
        mDotView.layout(mBottomNavigationItem.getWidth() / 2 + BarUtils.dip2px(getContext(), 6)
                , dotTop
                , mBottomNavigationItem.getWidth() / 2 + BarUtils.dip2px(getContext(), 6) + mDotView.getWidth()
                , dotTop + mDotView.getHeight());
    }


    /**
     * 设置显示的数字
     *
     * @param num 0不显示 -1显示小点 >99显示99+
     */
    public void showNum(int num) {
        hasMesPoint = true;
        mDotView.showNum(num);
    }

    /**
     * 消费该位置的数字
     */
    public void disMissMes() {
        if(!hasMesPoint)return;
        hasMesPoint = false;
        mDotView.disMisMes();
    }


    /**
     * 改变DotView的位置
     *
     * @param top
     */
    public void correctDotViewPosition(int top) {
        if (!hasMesPoint) return;
        dotTop = top;
        mDotView.layout(mBottomNavigationItem.getWidth()/2+BarUtils.dip2px(getContext(),6)
                , top
                ,mBottomNavigationItem.getWidth()/2+BarUtils.dip2px(getContext(),6)+mDotView.getWidth()
                ,top+mDotView.getHeight());
    }

    public void setDotTop(int dotTop) {
        this.dotTop = dotTop;
    }
}
