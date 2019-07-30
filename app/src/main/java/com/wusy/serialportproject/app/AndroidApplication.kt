package com.wusy.serialportproject.app

import android.content.Intent
import com.wusy.serialportproject.service.SerialPortService
import com.wusy.wusylibrary.base.BaseApplication

class AndroidApplication : BaseApplication(){
    override fun onCreate() {
        super.onCreate()
        startSerialPortService()
    }

    private fun startSerialPortService(){
        var intent=Intent(this,SerialPortService::class.java)
        startService(intent)
    }
}
