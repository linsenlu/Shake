package com.example.shake;

import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    private MediaPlayer player;
    private Vibrator vibrator;  //声明震动对象
    private int FACTOR = 15;    //加速传感器的阈值
    private boolean isShake;    //保障一次动画执行完才触发下一次shake()，默认为false

    SensorEventListener sensorEventListener = new SensorEventListener() {

        //传感器数据改变的回调
        @Override
        public void onSensorChanged(SensorEvent event) {

            //判断传感器的类型
            if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){     //加速度传感器
                // values的值最多只有三个
                float[] values = event.values;
                float valuesX = values[0];
                float valuesY = values[1];
                float valuesZ = values[2];

                //如果values的值超过阈值，执行相应动作
                if (valuesX>FACTOR || valuesX<-FACTOR ||valuesY>FACTOR || valuesY<-FACTOR || valuesZ>FACTOR || valuesZ <-FACTOR){
                    //模拟微信摇一摇
                    if (isShake){
                        return;
                    }
                    isShake = true;
                    shake();
                }

            }
            if (event.sensor.getType()==Sensor.TYPE_LIGHT){         //光感传感器
                float light = event.values[0];
                Log.e("print","光感信号：====="+light);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //传感器频率改变的回掉
        }
    };
    private AnimationDrawable ad;

    //从声音、视觉、触觉模拟微信摇一摇
    private void shake() {
        //播放声音
        player.start();
        //动画
        ad = (AnimationDrawable) iv.getBackground();
        ad.start();
        //震动
        long[] pattern = new long[]{500, 500, 500, 500};    // 震动的规则:停顿/震动/停顿/震动
        /**
         * 只有1个参数的时候，第一个参数用来指定振动的毫秒数。
         要传递2个参数的时候，第1个参数用来指定振动时间的样本，第2个参数用来指定是否需要循环:-1表示只震动一次，为0则震动会一直持续。
         振动时间的样本是指振动时间和等待时间的交互指定的数组。
         */
        vibrator.vibrate(pattern, 0);
        //动画结束后停止摇一摇
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // TODO 停止摇一摇
                vibrator.cancel();
                ad.stop();
                isShake = false;
            }
        }, 1800);


    }

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        player = MediaPlayer.create(this,R.raw.shake_sound_male);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);   //实列化
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取加速度传感器
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //获取光感传感器
        Sensor sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        /**
         * 注册监听
         * 参数一:传感器监听的回调 参数二:传感器对象 参数三:监听被回调的频率,一共4种,针对游戏,UI,普通等
         */
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener,sensorLight,SensorManager.SENSOR_DELAY_UI);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
