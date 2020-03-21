package com.wusy.serialportproject.ui.screen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.bean.OutSideTempBean
import com.wusy.serialportproject.ui.EnvAirActivity
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.wusylibrary.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_screen.*
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

import android.graphics.drawable.GradientDrawable



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
        tvTVOCCount.text=ed.tvoc.toString()
        val map=getAQIIconMap(ed.AQI)
        tvAQIQuality.text=map["text"]
        val mGradientDrawable = tvAQIQuality.background as GradientDrawable
        mGradientDrawable.setColor(Color.parseColor(map["color"]))
        tvAQIQuality.setTextColor(Color.parseColor(map["textColor"]))
        tvAQI.text= ed.AQI.toString()
        tvAQI.setTextColor(Color.parseColor(map["color"]))

    }
    fun initOutSideData(bean :OutSideTempBean){
        tvOutSideHumCount.text=(bean.result?.realtime?.humidity?:"")+"%"
        tvOutSideTempCount.text=(bean.result?.realtime?.temperature?:"")+" ℃"
        tvOutSideDirectCount.text=bean.result?.realtime?.direct?:"未知"
        tvOutSidePowerCount.text=bean.result?.realtime?.power?:"未知"
        tvOutSideInfoCount.text=bean.result?.realtime?.info?:"未知"
        val map=getAQIIconMap((bean.result?.realtime?.aqi?:"0").toInt())
        tvOutSideAQIQuality.text=map["text"]
        val mGradientDrawable = tvOutSideAQIQuality.background as GradientDrawable
        mGradientDrawable.setColor(Color.parseColor(map["color"]))
        tvOutSideAQIQuality.setTextColor(Color.parseColor(map["textColor"]))
        tvOutSideAQI.text=bean.result?.realtime?.aqi?:"0"
        tvOutSideAQI.setTextColor(Color.parseColor(map["color"]))
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
                    val enD = EnvironmentalDetector(intent.getStringExtra("data"),EnvAirActivity.currentEnv)
                    initDate(enD)
                }
            }
        }
    }
    fun getAQIIconMap(aqi:Int):HashMap<String,String>{
        var map=HashMap<String,String>()
        when(aqi){
            in 0..50->{
                map["color"]="#44d83f"
                map["text"]="优"
                map["textColor"]="#ffffff"
            }
            in 51..100->{
                map["color"]="#ffff00"
                map["text"]="良"
                map["textColor"]="#333333"
            }
            in 101..150->{
                map["color"]="#ff7e00"
                map["text"]="轻度污染"
                map["textColor"]="#ffffff"
            }
            in 151..200->{
                map["color"]="#fe0000"
                map["text"]="中度污染"
                map["textColor"]="#ffffff"
            }
            in 201..300->{
                map["color"]="#98004b"
                map["text"]="重度污染"
                map["textColor"]="#ffffff"
            }
            else->{
                map["color"]="#7e0123"
                map["text"]="严重污染"
                map["textColor"]="#ffffff"
            }
        }
        return map
    }
}
