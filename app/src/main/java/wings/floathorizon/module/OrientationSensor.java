package wings.floathorizon.module;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import static android.content.Context.SENSOR_SERVICE;

/**
 * 方向传感器
 * 单例
 * 调用函数initial以激活传感器
 * Created by Wings on 2017/2/26.
 */
public class OrientationSensor implements SensorEventListener
{
    /*静态*/
    public static OrientationSensor module;
    /*成员*/
    private SensorManager sensorManager;
    private Sensor accelerator;
    private Sensor magnetometer;
    private float[] accValues;
    private float[] magValues;
    private float[] rotateMatrix;       //旋转矩阵
    private float[] orientation;        //方向数据，弧度
    private Callback callback;      //回调

    static
    {
        module=new OrientationSensor();
    }

    /**
     * 构造函数
     * 私有
     */
    private OrientationSensor()
    {
        accValues=new float[3];
        magValues=new float[3];
        rotateMatrix=new float[9];
        orientation=new float[3];
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
            accValues=sensorEvent.values.clone();
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
            magValues=sensorEvent.values.clone();

        SensorManager.getRotationMatrix(rotateMatrix, null, accValues, magValues);
        SensorManager.getOrientation(rotateMatrix, orientation);

        if(callback!=null)
            callback.function(orientation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i){}

    /**
     * 初始化
     * @param activity 执行环境
     */
    public void initial(Activity activity,Callback callback)
    {
        if(accelerator==null || magnetometer==null)
        {
            sensorManager=(SensorManager)activity.getSystemService(SENSOR_SERVICE);
            accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, accelerator, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer,SensorManager.SENSOR_DELAY_NORMAL);

            if(callback!=null)
                this.callback=callback;
        }
    }

    /**
     * 关闭
     */
    public void close()
    {
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(this);
    }

    /**
     * 回调
     * 接口
     */
    public interface Callback
    {
        void function(final float[] orientation);
    }
}
