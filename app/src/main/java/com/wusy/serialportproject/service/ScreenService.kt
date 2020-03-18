package com.wusy.serialportproject.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.ui.screen.ScreenActivity
import com.wusy.serialportproject.ui.screen.ScreenClockActivity
import com.wusy.serialportproject.ui.screen.ScreenNumberClockActivity
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import java.util.*

class ScreenService: Service(){
    override fun onCreate() {
        super.onCreate()
        Logger.i("ScreenService star")
        screenTimer.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY

    }
    /**
     * 这是一个1S的定时器，用于屏保
     */
    private var screenTimer: Thread = Thread(Runnable {
        while (true) {
            Thread.sleep(1000)
            val timeNow = Date(System.currentTimeMillis())
            /* 计算User静止不动作的时间间距 */
            /**当前的系统时间 - 上次触摸屏幕的时间 = 静止不动的时间 */
            val timePeriod = timeNow.time - Constants.lastUpdateTime.time
            /*将静止时间毫秒换算成秒*/
            val timePeriodSecond = timePeriod.toFloat() / 1000
            /* 获取设置的间隔时间*/
            val timeDistanceStr=SharedPreferencesUtil.getInstance(this).getData(Constants.SCREEN_SETTING_TIME,Constants.DEFAULT_SCREEN_DISTANCE_TIME) as String
            val timeDistance = when(timeDistanceStr){
                "从不"->{
                    0
                }
                else->{
                    timeDistanceStr.replace("分钟","").toInt()*60
                }
            }
            if (timePeriodSecond > timeDistance) {
                if(timeDistance==0) return@Runnable
                if (!Constants.isOpenScreen) {  //说明没有进入屏保
                    /* 启动线程去显示屏保 */
                    showScreenServer()
                } else {
                    /*屏保正在显示中*/
                }
            } else {
//                Logger.i("静止市场未到，时间为${timeDistance}  已静止${timePeriodSecond}")
                /*说明静止之间没有超过规定时长*/
            }
        }
    })
    private fun showScreenServer(){
        var intent=Intent()
        when(SharedPreferencesUtil.getInstance(this).getData(Constants.SCREEN_SETTING_MODEL_TYPE,0)){
            0->{
               intent.setClass(this, ScreenActivity::class.java)
                Logger.i("正在打开屏保--空气质量")
            }
            1->{
                intent.setClass(this, ScreenClockActivity::class.java)
                Logger.i("正在打开屏保--模拟时钟")
            }
            2->{
                intent.setClass(this, ScreenNumberClockActivity::class.java)
                Logger.i("正在打开屏保--数字时钟")

            }
            else->{
                Logger.i("未设置屏保类型,当前state值=${SharedPreferencesUtil.getInstance(this).getData(Constants.SCREEN_SETTING_MODEL_TYPE,0)}")
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        Logger.i("ScreenService destroy")

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}