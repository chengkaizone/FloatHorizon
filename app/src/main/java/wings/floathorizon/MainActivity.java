package wings.floathorizon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import wings.floathorizon.activity.Introduction;
import wings.floathorizon.activity.Preference;
import wings.floathorizon.module.FixPart;
import wings.floathorizon.module.FloatPart;
import wings.floathorizon.module.OrientationSensor;
import wings.floathorizon.part.Gradienter;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    /*成员*/
    private int state;
    private int mode;
    private OrientationSensor orientationSensor;
    private FixPart fixPart;
    private FloatPart floatPart;
    private SharedPreferences sharedPreferences;
    private boolean screenOn;
    private float range;        //量程
    private float rangeInt;
    private TextView textX;
    private TextView textY;
    /*常量*/
    private static final int State_Main =0;       //模式：主界面
    private static final int State_Preference =1;     //模式：设置
    private static final int State_Introduction =2;     //模式：说明
    private static final int State_Hide =3;       //模式：隐藏
    private static final int DoubleHitInternal=2000;        //双击退出时间间隔
    private static final String Preference_Mode="Preference_Mode";      //模式设置存储
    public static final String Mode="Mode";

    /*重载*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /*界面初始化*/
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //隐藏标题栏
        setContentView(R.layout.activity_main);
        if(getSupportActionBar()!=null)     //隐藏标题栏
            getSupportActionBar().hide();
        /*状态*/
        state = State_Main;
        /*设置初始化*/
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);       //设置初始化
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        screenOn=sharedPreferences.getBoolean(getResources().getString(R.string.preference_screenOn),false);
        rangeInt=Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.preference_range),getResources().getString(R.string.preference_range_default)));
        this.range=rangeInt/360f*2*(float)Math.PI;
        mode=sharedPreferences.getInt(Preference_Mode,Gradienter.Mode_Aspect);
        /*水平仪初始化*/
        fixPart =new FixPart(this,mode);
        switchMode(mode,true);
        /*控件初始化*/
        ImageButton buttonExit=(ImageButton)findViewById(R.id.activityMain_buttonExit);     //按钮：退出
        buttonExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                exit();
            }
        });
        ImageButton buttonHide=(ImageButton)findViewById(R.id.activityMain_buttonHide);     //按钮：隐藏
        buttonHide.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                state = State_Hide;

                Intent intent= new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
        ImageButton buttonSet=(ImageButton)findViewById(R.id.activityMain_buttonSet);       //按钮：设置
        buttonSet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                state = State_Preference;

                Intent intent=new Intent(MainActivity.this,Preference.class);
                intent.putExtra(Mode,mode);
                startActivity(intent);
            }
        });
        ImageButton buttonIntroduction=(ImageButton)findViewById(R.id.activityMain_buttonIntroduction);     //按钮：说明
        buttonIntroduction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                state = State_Introduction;

                startActivity(new Intent(MainActivity.this,Introduction.class));
            }
        });
        final ImageButton buttonMode=(ImageButton)findViewById(R.id.activityMain_buttonMode);       //按钮：模式
        buttonMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popupMenu=new PopupMenu(MainActivity.this,buttonMode);
                popupMenu.getMenuInflater().inflate(R.menu.activitymain_mode,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch(item.getItemId())
                        {
                            case R.id.activityMain_mode_aspect:
                                switchMode(Gradienter.Mode_Aspect,false);
                                break;
                            case R.id.activityMain_mode_swing:
                                switchMode(Gradienter.Mode_Swing,false);
                                break;
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }
        });
        /*方向传感器*/
        orientationSensor = OrientationSensor.module;       //开启方向传感器
        orientationSensor.initial(this, new OrientationSensor.Callback()
        {
            @Override
            public void function(final float[] orientation)
            {
                switch(mode)
                {
                    case Gradienter.Mode_Aspect:
                        float rateX=-orientation[2]/ range;
                        if(rateX>1)
                            rateX=1;
                        else if(rateX<-1)
                            rateX=-1;
                        float rateY=orientation[1]/ range;
                        if(rateY>1)
                            rateY=1;
                        else if(rateY<-1)
                            rateY=-1;

                        switch (state)
                        {
                            case State_Main:
                                fixPart.setGraphicData(new Gradienter.GraphicData(rateX,rateY));
                                break;
                            case State_Hide:
                                if(floatPart!=null)
                                    floatPart.setGraphicData(new Gradienter.GraphicData(rateX,rateY));
                                break;
                        }

                    textX.setText(String.format(getResources().getString(R.string.activityMain_textX)+"%3d",(int)(rateX*rangeInt)));        //角度文字更新
                    textY.setText(String.format(getResources().getString(R.string.activityMain_textY)+"%3d",(int)(rateY*rangeInt)));
                        break;
                    case Gradienter.Mode_Swing:
                        float angle=orientation[2];
                        switch (state)
                        {
                            case State_Main:
                                fixPart.setGraphicData(new Gradienter.GraphicData(angle,0));
                                break;
                            case State_Hide:
                                if(floatPart!=null)
                                    floatPart.setGraphicData(new Gradienter.GraphicData(angle,(float)(angle+Math.PI/2)));
                                break;
                        }
                        break;
                }
            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        switch(state)
        {
            case State_Hide:
                if(floatPart!=null)
                    floatPart.remove();
                break;
        }
        if(screenOn)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        state = State_Main;
    }
    @Override
    protected void onPause()
    {
        super.onPause();

        switch(state)
        {
            case State_Hide:
                floatPart = new FloatPart(MainActivity.this,mode);
                break;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        orientationSensor.close();
    }
    @Override
    public void onBackPressed()     //双击返回键退出
    {
        if((System.currentTimeMillis()-exitTime)>DoubleHitInternal)
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.backTip),Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
        }
        else
            exit();
    }
    private long exitTime=0;
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)        //设置改变
    {
        if (s.equals(getResources().getString(R.string.preference_screenOn)))
        {
            screenOn = sharedPreferences.getBoolean(getResources().getString(R.string.preference_screenOn),false);
            if(screenOn)
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else if(s.equals(getResources().getString(R.string.preference_range)))
        {
            rangeInt=Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.preference_range),getResources().getString(R.string.preference_range_default)));
            this.range=rangeInt/360f*2*(float)Math.PI;
        }
    }

    /**
     * 退出
     */
    private void exit()
    {
        System.exit(0);
    }

    /**
     * 切换模式
     * @param mode 模式
     *             Mode_Aspect：平面模式
     *             Mode_Swing：摇摆模式
     * @param initial 是否是初始化
     */
    private void switchMode(int mode,boolean initial)
    {
        if(this.mode!=mode || initial)
        {
            this.mode=mode;
            sharedPreferences.edit().putInt(Preference_Mode,mode).apply();

            switch(mode)
            {
                case Gradienter.Mode_Aspect:
                    textX=(TextView)findViewById(R.id.mainActivity_textX);       //文本：X
                    textX.setText("X: 0");
                    textX.setVisibility(View.VISIBLE);
                    textY=(TextView)findViewById(R.id.mainActivity_textY);       //文本：Y
                    textY.setText("Y: 0");
                    textY.setVisibility(View.VISIBLE);
                    break;
                case Gradienter.Mode_Swing:
                    if(textX!=null)
                        textX.setVisibility(View.INVISIBLE);
                    if(textY!=null)
                        textY.setVisibility(View.INVISIBLE);
                    break;
            }

            if(!initial)
                fixPart.switchMode(mode);
        }
    }
}
