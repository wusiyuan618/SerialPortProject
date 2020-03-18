package com.wusy.serialportproject.ui.screen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.bean.OutSideTempBean
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.wusylibrary.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_screen.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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
        Thread(Runnable {
            //这是一个每1min执行一次的定时器，用于检测寄电器状态和环境状态
            while (true) {
                requestOutSideTemp()
                Thread.sleep(60*1000*60)
            }
        }).start()
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
        tvHumCount.text=ed.humidity.toString()+"%"
        tvCO2Count.text=ed.cO2.toString()+" μg/m³"
        tvPM25Count.text=ed.pM2_5.toString()+" μg/m³"
        tvHCHOCount.text=ed.formaldehyde.toString()+" μg/m³"
        tvTVOCCount.text=ed.tvoc.toString()+" μg/m³"
    }
    fun initOutSideData(bean :OutSideTempBean){
        tvOutSideAQI.text=bean.result?.realtime?.aqi?:""
        tvOutSideHumCount.text=(bean.result?.realtime?.humidity?:"")+"%"
        tvOutSideTempCount.text=(bean.result?.realtime?.temperature?:"")+" ℃"
        tvOutSideDirectCount.text=bean.result?.realtime?.direct?:"未知"
        tvOutSidePowerCount.text=bean.result?.realtime?.power?:"未知"
        tvOutSideInfoCount.text=bean.result?.realtime?.info?:"未知"
    }
    private fun requestOutSideTemp(){
        OkHttpUtil.getInstance().asynGet("http://apis.juhe.cn/simpleWeather/query?city=重庆&key=3139491a0853306108f5d44194dbf17d",object:OkHttpUtil.ResultCallBack{
            override fun successListener(call: Call?, response: Response?) {
                var str=response?.body()?.string()
                var bean = Gson().fromJson<OutSideTempBean>(str,OutSideTempBean::class.java)
                runOnUiThread {
                    initOutSideData(bean)
                }
            }

            override fun failListener(call: Call?, e: IOException?, message: String?) {
                showToast("无法获取户外天气，请检查网络")
            }

        })
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
