package com.wusy.serialportproject.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.util.CommonConfig
import kotlinx.android.synthetic.main.activity_screen.*

class ScreenActivity:BaseTouchActivity(){
    private lateinit var bradCast: ScreenBroadCast
    override fun findView() {
    }

    override fun init() {
        Logger.i("屏保启动")
        Constants.isOpenScreen=true
        layout_total.setOnClickListener {
            finish()
        }
        if(Constants.curED!=null){
            initDate(Constants.curED!!)
        }
        bradCast=ScreenBroadCast()
        var actions=ArrayList<String>()
        actions.add(CommonConfig.ACTION_ENVIRONMENTALDETECOTOR_DATA)
        addBroadcastAction(actions,bradCast)
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_screen
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i("屏保消失")
        Constants.isOpenScreen=false
    }
    @SuppressLint("SetTextI18n")
    private fun initDate(ed:EnvironmentalDetector){
        tvTempCount.text=ed.temp.toString()+" ℃"
        tvHumidityCount.text=ed.humidity.toString()+"%"
        tvCO2Count.text=ed.cO2.toString()+" μg/m³"
        tvPM25Count.text=ed.pM2_5.toString()+" μg/m³"
        tvPM25OutSideCount.text=ed.pM2_5OutDoor.toString()+" μg/m³"
        tvHCHOCount.text=ed.formaldehyde.toString()+" μg/m³"
        tvTVOCCount.text=ed.tvoc.toString()+" μg/m³"
    }
    open inner class ScreenBroadCast:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                CommonConfig.ACTION_ENVIRONMENTALDETECOTOR_DATA->{
                    val enD = EnvironmentalDetector(intent.getStringExtra("data"))
                    initDate(enD)
                }
            }
        }

    }
}
