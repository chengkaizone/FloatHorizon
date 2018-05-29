package wings.floathorizon.module;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import wings.floathorizon.R;
import wings.floathorizon.part.Aspect;
import wings.floathorizon.part.Gradienter;
import wings.floathorizon.part.Swing;

import static android.content.Context.WINDOW_SERVICE;

/**
 * 浮游部件
 * Created by Wings on 2017/2/27.
 */
public class FloatPart
{
    /*成员*/
    private int statusBarHeight;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Gradienter gradienter;
    /*常量*/
    private static final int DoubleHitInternal=200;

    /**
     * 构造方法
     * @param activity 活动
     * @param mode 模式
     *             Mode_Aspect：平面模式
     *             Mode_Swing：摇摆模式
     */
    public FloatPart(Activity activity,int mode)
    {
        switch(mode)
        {
            case Gradienter.Mode_Aspect:
                gradienter=new Aspect(activity, Aspect.Type_Float);
                break;
            case Gradienter.Mode_Swing:
                gradienter=new Swing(activity,Gradienter.Type_Float);
                break;
        }

        int resourceId=activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight=activity.getResources().getDimensionPixelSize(resourceId);

        gradienter.setOnTouchListener(new View.OnTouchListener()
        {
            private float viewX;
            private float viewY;
            private long exitTime=0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                float screenX=motionEvent.getRawX();
                float screenY=motionEvent.getRawY()-statusBarHeight;

                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        viewX=motionEvent.getX();
                        viewY=motionEvent.getY();

                        if((System.currentTimeMillis()-exitTime)>DoubleHitInternal)       //双击退出
                            exitTime=System.currentTimeMillis();
                        else
                            System.exit(0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x=(int)(screenX-viewX);
                        layoutParams.y=(int)(screenY-viewY);
                        windowManager.updateViewLayout(view,layoutParams);
                        break;
                }

                return true;
            }
        });

        windowManager=(WindowManager)activity.getApplicationContext().getSystemService(WINDOW_SERVICE);      //取得系统窗体
        layoutParams=new WindowManager.LayoutParams();       //窗体的布局样式
        layoutParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     //设置窗体显示类型（系统提示）
        layoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;       //设置窗体焦点及触摸（不能获得按键和输入焦点）
        layoutParams.format= PixelFormat.RGBA_8888;     //设置显示模式
        layoutParams.gravity=Gravity.TOP|Gravity.START;
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float size=activity.getResources().getDimension(R.dimen.gradienter_reduced);
        int originX = displayMetrics.widthPixels / 2 - (int) (size / 2);
        int originY = displayMetrics.heightPixels / 2 - (int) (size / 2) - (int) (statusBarHeight / displayMetrics.density);
        layoutParams.width=(int)size;
        layoutParams.height=(int)size;
        layoutParams.x= originX;
        layoutParams.y= originY;
        windowManager.addView(gradienter,layoutParams);
    }

    /**
     * 去除部件
     */
    public void remove()
    {
        windowManager.removeViewImmediate(gradienter);
    }

    /**
     * 设置图形数据
     * @param graphicData 图形数据
     */
    public void setGraphicData(Gradienter.GraphicData graphicData)
    {
        float data1=graphicData.data1;
        float data2=graphicData.data2;

        if(gradienter !=null)
        {
            switch (windowManager.getDefaultDisplay().getRotation())        //针对屏幕旋转进行数据匹配
            {
                case Surface.ROTATION_90:
                    data1 = graphicData.data2;
                    data2 = -graphicData.data1;
                    break;
                case Surface.ROTATION_270:
                    data1 = -graphicData.data2;
                    data2 = graphicData.data1;
                    break;
                case Surface.ROTATION_180:
                    data1 = -graphicData.data1;
                    data2 = -graphicData.data2;
                    break;
                case Surface.ROTATION_0:
                    break;
            }

            gradienter.setGraphicData(new Gradienter.GraphicData(data1,data2));
        }
    }
}



