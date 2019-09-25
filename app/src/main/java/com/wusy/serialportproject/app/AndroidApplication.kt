package com.wusy.serialportproject.app

import android.content.Intent
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.service.SerialPortService
import com.wusy.serialportproject.ui.EnvAirActivity
import com.wusy.wusylibrary.base.BaseApplication

class AndroidApplication : BaseApplication(){
    private val restartHandler = Thread.UncaughtExceptionHandler { _, ex ->
        Logger.e(ex, Thread.currentThread().toString() + "--APP异常捕获uncaughtException")
//        restartApp()
    }
    override fun onCreate() {
        super.onCreate()
        startSerialPortService()
        Thread.setDefaultUncaughtExceptionHandler(restartHandler) // 程序崩溃时触发线程  以下用来捕获程序崩溃异常

    }

    private fun startSerialPortService(){
        var intent=Intent(this,SerialPortService::class.java)
        startService(intent)
    }
    private fun restartApp() {
        Logger.e("应用开始重启")
        val intent = Intent(this, EnvAirActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }
}
