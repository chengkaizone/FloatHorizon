package wings.floathorizon.part;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import wings.floathorizon.R;

/**
 * 水平仪（平面模式）
 * 不可用XML方式实现
 * Created by Wings on 2017/3/8.
 */
@SuppressLint("ViewConstructor")
public class Aspect extends Gradienter
{
    /*成员*/
    private Point point;
    private float pointRadius;
    private float activeRadius;
    private float rateX;
    private float rateY;
    private float targetX;
    private float targetY;
    private int pointColor;
    private int pointColorTargeted;

    /**
     * 构造方法
     * @param context 环境
     * @param type 类型
     */
    public Aspect(Context context, int type)
    {
        super(context,type);

        if(type== Type_Float)
            pointRadius=getResources().getDimension(R.dimen.aspect_pointRadius_reduced);
        else
            pointRadius=getResources().getDimension(R.dimen.aspect_pointRadius);
        //noinspection deprecation
        pointColor=getResources().getColor(R.color.aspect_point);
        //noinspection deprecation
        pointColorTargeted=getResources().getColor(R.color.aspect_pointTargeted);

        //noinspection deprecation
        frameLayout.findViewById(R.id.gradienter_background).setBackground(getResources().getDrawable(R.drawable.aspect_background));     //设置背景

        point =new Point(context);
        part=point;
        LayoutParams layoutParams =new LayoutParams((int)pointRadius*2, (int)pointRadius*2);
        frameLayout.addView(point,layoutParams);
    }

    @Override
    void refresh()
    {
        point.dataFit();

        if((Math.abs(rateX)<0.03&&Math.abs(rateY)<0.03))        //点进入圆，变色
        {
            if(!targeted)
            {
                point.paint.setColor(pointColorTargeted);
                point.invalidate();
            }

            targeted=true;
        }
        else
        {
            if(targeted)
            {
                point.paint.setColor(pointColor);
                point.invalidate();
            }

            targeted=false;
        }

        if(moveX!=null)
            moveX.cancel();
        moveX=ObjectAnimator.ofFloat(point,"translationX", targetX);
        moveX.setDuration(AnimationPeriod).start();
        if(moveY!=null)
            moveY.cancel();
        moveY=ObjectAnimator.ofFloat(point,"translationY", targetY);
        moveY.setDuration(AnimationPeriod).start();
    }
    private ObjectAnimator moveX;
    private ObjectAnimator moveY;
    private boolean targeted=false;
    @Override
    public void setGraphicData(GraphicData graphicData)
    {
        rateX=graphicData.data1;
        rateY=graphicData.data2;
    }

    /**
     * 指示点
     * 内部静态类
     */
    @SuppressLint("ViewConstructor")
    class Point extends View
    {
        /*成员*/
        public Paint paint;

        /**
         * 构造方法
         * @param context 环境
         */
        public Point(Context context)
        {
            super(context);

            paint =new Paint();
            //noinspection deprecation
            paint.setColor(getResources().getColor(R.color.aspect_point));
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            float backgroundRate=Float.parseFloat(getResources().getString(R.string.aspect_backgroundRate));
            activeRadius=size/2*backgroundRate;

            setTranslationX(size/2- pointRadius);
            setTranslationY(size/2- pointRadius);
        }

        /*重载*/
        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            canvas.drawCircle(pointRadius, pointRadius, pointRadius, paint);
        }

        /**
         * 正方形区域映射为圆形
         */
        private void dataFit()
        {
            float rate=1;
            float angle=(float)Math.abs(Math.atan(rateY/rateX));
            if(angle>0 || angle<Math.PI/2 )
            {
                float maxLength;
                if(angle<=Math.PI/4)
                    maxLength=(float)(1/Math.cos(angle));
                else
                    maxLength=(float)(1/Math.cos(Math.PI/2-angle));

                rate = 1/maxLength;
            }

            rateX*=rate;
            rateY*=rate;
            targetX=size/2- pointRadius +rateX*activeRadius;
            targetY=size/2- pointRadius +rateY*activeRadius;
        }
    }
}


