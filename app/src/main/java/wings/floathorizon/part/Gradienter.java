package wings.floathorizon.part;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

import wings.floathorizon.R;

/**
 * 水平仪
 * 抽象类
 * Created by Wings on 2017/3/18.
 */
public abstract class Gradienter extends FrameLayout
{
    /*变量*/
    FrameLayout frameLayout;
    int size;
    View part;
    /*常量*/
    public static final int Type_Fix =0;
    public static final int Type_Float =1;
    public static final int Mode_Aspect =0;
    public static final int Mode_Swing=1;
    private static final int RefreshPeriod=100;
    static final int AnimationPeriod=250;

    /**
     * 构造函数
     * @param context 环境
     */
    public Gradienter(Context context,int type)
    {
        super(context);

        frameLayout= (FrameLayout) LayoutInflater.from(context).inflate(R.layout.gradienter,this);
        if(type== Type_Float)
            size=(int)getResources().getDimension(R.dimen.gradienter_reduced);
        else
            size=(int)getResources().getDimension(R.dimen.gradienter);

        TimerTask timerTask=new TimerTask()     //限制刷新频率
        {
            @Override
            public void run()
            {
                if(part!=null)
                    part.post(new Runnable ()
                    {
                        @Override
                        public void run()
                        {
                            refresh();
                        }
                    });
            }
        };
        Timer refresh=new Timer();
        refresh.schedule(timerTask,0,RefreshPeriod);
    }

    /**
     * 刷新
     * 抽象方法
     */
    abstract void refresh();

    /**
     * 设置图形数据
     * @param graphicData 图形数据
     */
    public abstract void setGraphicData(GraphicData graphicData);

    /**
     * 图形数据
     * 静态类
     */
    public static class GraphicData
    {
        /*成员*/
        public float data1;
        public float data2;

        /**
         * 构造方法
         * @param data1 数据1
         * @param data2 数据2
         */
        public GraphicData(float data1,float data2)
        {
            this.data1=data1;
            this.data2=data2;
        }
    }
}
