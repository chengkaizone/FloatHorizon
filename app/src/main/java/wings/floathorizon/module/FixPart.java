package wings.floathorizon.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.RelativeLayout;

import wings.floathorizon.R;
import wings.floathorizon.part.Aspect;
import wings.floathorizon.part.Gradienter;
import wings.floathorizon.part.Swing;

/**
 * 固定部件
 * 不可用XML方式实现
 * Created by Wings on 2017/3/18.
 */
@SuppressLint("ViewConstructor")
public class FixPart
{
    /*成员*/
    private Gradienter gradienter;
    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams layoutParams;
    private Activity activity;

    /**
     * 构造方法
     * @param activity 环境
     * @param mode 模式
     *             Mode_Aspect：平面模式
     *             Mode_Swing：摇摆模式
     */
    public FixPart(Activity activity,int mode)
    {
        this.activity=activity;

        switch(mode)
        {
            case Gradienter.Mode_Aspect:
                gradienter=new Aspect(activity, Gradienter.Type_Fix);
                break;
            case Gradienter.Mode_Swing:
                gradienter=new Swing(activity,Gradienter.Type_Fix);
                break;
        }

        relativeLayout=(RelativeLayout)activity.findViewById(R.id.activity_main);
        int size=(int)activity.getResources().getDimension(R.dimen.gradienter);
        layoutParams=new RelativeLayout.LayoutParams(size,size);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

        relativeLayout.addView(gradienter,layoutParams);
    }

    /**
     * 设置图形数据
     * @param graphicData 图形数据
     */
    public void setGraphicData(Gradienter.GraphicData graphicData)
    {
        gradienter.setGraphicData(graphicData);
    }

    /**
     * 切换模式
     * @param mode 模式
     *             Mode_Aspect：平面模式
     *             Mode_Swing：摇摆模式
     */
    public void switchMode(int mode)
    {
        relativeLayout.removeView(gradienter);
        switch(mode)
        {
            case Gradienter.Mode_Aspect:
                gradienter=new Aspect(activity, Gradienter.Type_Fix);
                break;
            case Gradienter.Mode_Swing:
                gradienter=new Swing(activity,Gradienter.Type_Fix);
                break;
        }
        relativeLayout.addView(gradienter,layoutParams);
    }
}
