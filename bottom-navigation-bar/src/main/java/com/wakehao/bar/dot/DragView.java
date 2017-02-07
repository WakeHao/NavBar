package com.wakehao.bar.dot;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wakehao.bar.R;

//import com.nineoldandroids.animation.Animator;
//import com.nineoldandroids.animation.ValueAnimator;
//import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * Created by WakeHao on 2016/11/16.
 * 拖拽视图
 */

public class DragView extends FrameLayout {

    private PointF dragCirclePoint;
    private Paint mDragPaint;
    private int statusBarHeight;
    private Context mContext;
    private PointF stillCirclePoint;
    //贝塞尔曲线的基准点
    private PointF middleBase;
    //绘制的拖拽圆显示的数字
    private String showNum="";
    //静态圆半径
    private float radius;
    private int textWidth;
    private int textHeight;

    private boolean isCanDraw;

    private float zoomedStillCircleRadius;
    //静态圆边缘点
    private PointF[] stillPoint;

    //拖拽圆边缘点
    private PointF[] dragPoint;

    //是否断开
    private boolean isDisconnet;

    //是否绘制文字 StillCirclr在点击的时候绘制 拖动的时候不绘制
    private boolean isDrawText;

    //控制在拖动的时候拖动DragCircle
    private boolean isShowDragCircle;
    private double slope;
    //超过此距离断开
    private float disconnectDistance=70f;
    //爆炸动画依赖view
    private ImageView boomImageView;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            destroy();
        }
    };

    //是否抖动
    public DragView(Context context) {

        this(context,null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    private void init() {
        ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((Activity) mContext).addContentView(this,lp);

        setBackgroundColor(Color.TRANSPARENT);
        dragCirclePoint=new PointF();
        mDragPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDragPaint.setColor(Color.RED);
        mDragPaint.setStyle(Paint.Style.FILL);
        mDragPaint.setTextSize(Utils.sp2px(mContext,8));

        stillCirclePoint=new PointF();

        boomImageView = new ImageView(mContext);
        boomImageView.setLayoutParams(new ViewGroup.LayoutParams(Utils.dip2px(mContext,50), Utils.dip2px(mContext,50)));
        boomImageView.setImageResource(R.drawable.drag_boom);
        boomImageView.setVisibility(INVISIBLE);
        addView(boomImageView);
    }


    private DotView mDotView;
    private long lastTime=0;
    /**
     * 取消滑动监听
     */
    public void removeOnTouchListener()
    {
        mDotView.setOnTouchListener(null);
    }
    public void rely(DotView relyView)
    {
//        bringToFront();
        mDotView=relyView;
        //小红点不设置拖拽监听
        if(relyView.isShowJustDot())return;
        relyView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    //点击时候画带消息数的圆圈，隐藏DotView
                    case MotionEvent.ACTION_DOWN:
                            //获取DotView的相关信息
                            getDotViewInfo(((DotView) v));
                            updataPointF(stillCirclePoint,((DotView) v).getCircleCenterOnRaw()[0]+radius,((DotView) v).getCircleCenterOnRaw()[1]+radius);
                            isCanDraw=true;
                            v.setVisibility(GONE);
                            isDrawText=true;
                            refreshView();
                            break;

                    //移动时候画不带消息静止圆，以及贝塞尔曲线和拖拽圆
                    case MotionEvent.ACTION_MOVE:
                        isDrawText=false;
                        isShowDragCircle=true;
                        dragCirclePoint.set(event.getRawX(),event.getRawY());
                        //拖动时候更新静止圆的边缘数组坐标
                        slope = ((event.getRawY() - stillCirclePoint.y)/(event.getRawX() - stillCirclePoint.x));
                        calculatePath();
                        refreshView();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isDisconnet)
                        {
                            isCanDraw=false;
                            isShowDragCircle=false;
                            refreshView();
                            v.setVisibility(GONE);
                            //显示爆炸效果
                            showBoomAnim();
                        }
                        //显示回弹效果
                        else showReBoundAnim(v);
                        break;
                }
                return true;
            }
        });
    }



    private void showReBoundAnim(final View v) {
        //回弹动画
        final PointF tempDragCenter = new PointF(dragCirclePoint.x, dragCirclePoint.y);

        final ValueAnimator mAnim = ValueAnimator.ofFloat(1.0f);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator mAnim) {
                // 0.0 -> 1.0f
                float percent = mAnim.getAnimatedFraction();
                PointF p = GeometryUtil.getPointByPercent(tempDragCenter, stillCirclePoint, percent);
//                                    updateDragCenter(p.x, p.y);
                dragCirclePoint.set(p.x,p.y);
                dragPoint = GeometryUtil.getIntersectionPoints(dragCirclePoint, radius, slope);
                middleBase= GeometryUtil.getMiddlePoint(stillCirclePoint,dragCirclePoint);
                refreshView();
            }
        });
        mAnim.setInterpolator(new OvershootInterpolator(4));
        mAnim.setDuration(500);
        mAnim.start();
        mAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isCanDraw=false;
                isShowDragCircle=false;
                isDisconnet=false;
                v.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 播放爆炸帧动画
     */
    private void showBoomAnim() {
        boomImageView.setX(dragCirclePoint.x-boomImageView.getWidth()/2);
        boomImageView.setY(dragCirclePoint.y-boomImageView.getHeight()/2-statusBarHeight);
        boomImageView.setVisibility(VISIBLE);
        AnimationDrawable boom_drawable = (AnimationDrawable) boomImageView.getDrawable();
        boom_drawable.start();
        handler.sendEmptyMessageDelayed(1,700);
    }

    /**
     * 得到被寄生的DotView的一些信息
     * 包括显示数字 位置 半径大小 圆心坐标(画StillCircle)
     * @param relyView
     */
    private void getDotViewInfo(DotView relyView) {
        showNum=relyView.getShowNum();
        radius=relyView.getRadius();
        textWidth=relyView.getNumWidth();
        textHeight=relyView.getNumHeight();
    }



    public void refreshView()
    {
        if (Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        } else{
            postInvalidate();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isCanDraw)
        {
            canvas.save();
            canvas.translate(0,-statusBarHeight);
            if(!isDisconnet)
            {
                //画拖拽圆
                if(isShowDragCircle)
                {
                    paintStatueCircle();
                    canvas.drawCircle(stillCirclePoint.x,stillCirclePoint.y,zoomedStillCircleRadius,mDragPaint);
                    drawBPath(canvas);
                    drawDragCircle(canvas);
                }
                else
                {
                    paintStatueCircle();
                    canvas.drawCircle(stillCirclePoint.x,stillCirclePoint.y,radius,mDragPaint);
                }
                if(isDrawText)
                {
                    paintStatueText();
                    if(showNum.equals("1"))mDragPaint.setTextSize(Utils.sp2px(mContext,9));
                    canvas.drawText(showNum,stillCirclePoint.x-textWidth/2-(showNum.contains("1")?Utils.dip2px(mContext,1):0),stillCirclePoint.y+textHeight/2,mDragPaint);
                }
            }
            if(isShowDragCircle)
            {
                drawDragCircle(canvas);
            }
            canvas.restore();
        }
    }

    /**
     * 绘画拖拽圆
     * @param canvas
     */
    private void drawDragCircle(Canvas canvas)
    {
        paintStatueCircle();
        canvas.drawCircle(dragCirclePoint.x,dragCirclePoint.y,radius,mDragPaint);
        paintStatueText();
        canvas.drawText(showNum,dragCirclePoint.x-textWidth/2,dragCirclePoint.y+textHeight/2,mDragPaint);
    }
    /**
     * 绘制贝塞尔曲线图
     * @param canvas
     */
    private void drawBPath(Canvas canvas) {
        Path path=new Path();
        path.moveTo(stillPoint[0].x,stillPoint[0].y);
        path.quadTo(middleBase.x,middleBase.y,dragPoint[0].x,dragPoint[0].y);
        path.lineTo(dragPoint[1].x,dragPoint[1].y);
        path.quadTo(middleBase.x,middleBase.y,stillPoint[1].x,stillPoint[1].y);
        path.close();
        canvas.drawPath(path,mDragPaint);
    }

    /**
     * move的时候计算贝塞尔曲线
     */
    private void calculatePath() {
        if(middleBase==null)middleBase=new PointF();
        if(stillPoint==null)stillPoint=new PointF[2];
        if(dragPoint==null)dragPoint=new PointF[2];
        zoomedStillCircleRadius=getZoomedStillRadius();
        stillPoint = GeometryUtil.getIntersectionPoints(stillCirclePoint,zoomedStillCircleRadius , slope);
        dragPoint = GeometryUtil.getIntersectionPoints(dragCirclePoint, radius, slope);
        middleBase= GeometryUtil.getMiddlePoint(stillCirclePoint,dragCirclePoint);

    }

    /**
     * 设置画笔为绘制文本模式
     */
    private void paintStatueText()
    {
        if(mDragPaint.getColor()== Color.RED)
        {
            mDragPaint.setColor(Color.WHITE);
            mDragPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mDragPaint.setStrokeWidth(1);
        }
    }
    /**
     * 设置画笔为绘制红点模式
     */
    private void paintStatueCircle()
    {
        if(mDragPaint.getColor()== Color.WHITE)
        {
            mDragPaint.setColor(Color.RED);
            mDragPaint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * 随着拉伸距离变大 静止圆绘制的半径变小
     * @return StillCircle的半径
     */
    private float getZoomedStillRadius()
    {
        float currentDistance=GeometryUtil.getDistanceBetween2Points(stillCirclePoint,dragCirclePoint);
        if(currentDistance>disconnectDistance)
        {
            currentDistance=disconnectDistance;
            //在此断开链接
            isDisconnet=true;
        }
        else
        {
            isDisconnet=false;
        }
        currentDistance= Math.min(currentDistance,disconnectDistance);
        float presnet=currentDistance/disconnectDistance;
        return envaluate(presnet,radius,0.3f*radius);
    }

    public float envaluate(float present,float start,float end)
    {
        return start+(end-start)*present;
    }

    /**
     * 爆炸动画播放完毕该view彻底销毁
     */
    private void destroy()
    {
        if(handler!=null)handler.removeCallbacksAndMessages(null);
        removeView(boomImageView);
        boomImageView=null;
        setVisibility(GONE);
        removeOnTouchListener();
        mContext=null;
    }

    public void onDestroy()
    {
        destroy();
    }
    /**
     * 代码调用消息不显示
     */
    public void disMissMes()
    {
        destroy();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        statusBarHeight = Utils.getStatusAndTitleBarHeight(this);
    }


    private void updataPointF(PointF pointF, float x, float y)
    {
        pointF.set(x,y);
    }

}
