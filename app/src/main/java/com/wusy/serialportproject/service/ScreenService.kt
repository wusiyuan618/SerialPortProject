package com.wusy.serialportproject.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.ui.ScreenActivity
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
                /*说明静止之间没有超过规定时长*/
            }
        }
    })
    private fun showScreenServer(){
        var intent=Intent(this,ScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}