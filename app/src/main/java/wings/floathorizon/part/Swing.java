package wings.floathorizon.part;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;

import wings.floathorizon.R;

/**
 * 水平仪（平面模式）
 * 不可用XML方式实现
 * Created by Wings on 2017/3/18.
 */
@SuppressLint("ViewConstructor")
public class Swing extends Gradienter
{
    /*成员*/
    private Line line;
    private float angle;
    private float lineWidth;
    private float lineRadius;
    private int lineColor;
    private int lineColorTargeted;

    /**
     * 构造方法
     * @param context 环境
     * @param type 类型
     */
    public Swing(Context context, int type)
    {
        super(context,type);

        if(type== Type_Float)
            lineWidth=getResources().getDimension(R.dimen.swing_lineWidth_reduced);
        else
            lineWidth=getResources().getDimension(R.dimen.swing_lineWidth);
        lineRadius=size/2*Float.parseFloat(getResources().getString(R.string.swing_backgroundRate));
        //noinspection deprecation
        lineColor=getResources().getColor(R.color.swing_line);
        //noinspection deprecation
        lineColorTargeted=getResources().getColor(R.color.swing_lineTargeted);

        //noinspection deprecation
        frameLayout.findViewById(R.id.gradienter_background).setBackground(getResources().getDrawable(R.drawable.swing_background));     //设置背景

        line=new Line(context);
        part=line;
        LayoutParams layoutParams =new LayoutParams(size, size);
        frameLayout.addView(line,layoutParams);
    }

    @Override
    void refresh()
    {
        if(Math.abs(angle)<5 && !targeted)       //线水平，变色
        {
            line.paint.setColor(lineColorTargeted);
            line.invalidate();

            targeted=true;
        }
        else if(Math.abs(angle)>=5 && targeted)
        {
            line.paint.setColor(lineColor);
            line.invalidate();

            targeted=false;
        }

        if(rotate!=null)
            rotate.cancel();
        rotate=ObjectAnimator.ofFloat(line,"rotation",angle);
        rotate.setDuration(AnimationPeriod).start();
    }
    private ObjectAnimator rotate;
    private boolean targeted=false;
    @Override
    public void setGraphicData(GraphicData graphicData)
    {
        float angleRadius=graphicData.data1;

        angle=-(float)(angleRadius/(2*Math.PI)*360);
    }

    /**
     * 指示线
     * 内部类
     */
    class Line extends View
    {
        /*成员*/
        public Paint paint;

        /**
         * 构造方法
         * @param context 环境
         */
        public Line(Context context)
        {
            super(context);

            paint =new Paint();
            //noinspection deprecation
            paint.setColor(lineColor);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(lineWidth);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                canvas.drawRoundRect(size/2-lineRadius,size/2-lineWidth/2,size/2+lineRadius,size/2+lineWidth/2,lineWidth/2,lineWidth/2,paint);
            else
                canvas.drawLine(size/2-lineRadius,size/2 ,size/2+lineRadius,size/2, paint);
        }
    }
}
