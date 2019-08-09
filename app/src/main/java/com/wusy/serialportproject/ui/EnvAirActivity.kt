package com.wusy.serialportproject.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.RED
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.bean.SendBean
import com.wusy.serialportproject.proxy.ClickProxy
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.serialportproject.util.DataUtils
import com.wusy.serialportproject.util.JDQType
import com.wusy.serialportproject.util.SerialCMD
import com.wusy.wusylibrary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_env_air.*
import java.util.ArrayList


class EnvAirActivity : BaseActivity() {
    companion object {
        /**
         * 制冷
         */
        const val MODE_Cryogen = 0
        /**
         * 制热
         */
        const val MODE_Heating = 1
        /**
         * 除湿
         */
        const val MODE_Dehumidification = 2
        /**
         * 加湿
         */
        const val MODE_Humidification = 3
        /**
         * 吹风
         */
        const val MODE_Hairdryer = 11

        /**
         * 当前使用的继电器
         */
        const val currentJDQType = JDQType.ZZIO1600
    }


    private var boradCast: EnvAirBoradCast? = null
    private val buffer = StringBuffer()
    private var sendBean: SendBean = SendBean()
    private var isStartAutoCryogen = false

    private var lastClickTime: Long = 0
    private val times: Long = 1000
    override fun getContentViewId(): Int {
        return R.layout.activity_env_air
    }


    override fun findView() {
    }

    override fun init() {
        initClick()
        initBroadCast()
        initThread()

    }

    private fun initThread() {
        Thread(Runnable {
            //这是一个每1min执行一次的定时器
            while (true) {
                buffer.delete(0, buffer.length)//定时更新下数据存储器，防止出现骚问题
                sendSerial(SerialCMD.EnvironmenttalSearch)
                Thread.sleep(1000)
                sendJDQSearch()
                Thread.sleep(60000)
            }
        }).start()
    }

    private fun initBroadCast() {
        boradCast = EnvAirBoradCast()
        var actionList = ArrayList<String>()
        actionList.add(CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI)
        addBroadcastAction(actionList, boradCast)
    }

    private fun initClick() {
        envAirCryogen.setOnClickListener(ClickProxy(View.OnClickListener {
            if (!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirCryogen, !envAirCryogen.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = MODE_Cryogen
                isOpen = envAirCryogen.isSelected
            }
            sendJDQControl()
        }, this))
        envAirHeating.setOnClickListener(ClickProxy(View.OnClickListener {
            if (!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirHeating, !envAirHeating.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = MODE_Heating
                isOpen = envAirHeating.isSelected

            }
            sendJDQControl()
        }, this))
        envAirDehumidification.setOnClickListener(ClickProxy(View.OnClickListener {
            if (!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirDehumidification, !envAirDehumidification.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = MODE_Dehumidification
                isOpen = envAirDehumidification.isSelected

            }
            sendJDQControl()
        }, this))
        envAirHumidification.setOnClickListener(ClickProxy(View.OnClickListener {
            if (!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirHumidification, !envAirHumidification.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = MODE_Humidification
                isOpen = envAirHumidification.isSelected

            }
            sendJDQControl()
        }, this))
        envAirHairdryer.setOnClickListener(ClickProxy(View.OnClickListener {
            if (!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirHairdryer, !envAirHairdryer.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = MODE_Hairdryer
                isOpen = envAirHairdryer.isSelected

            }
            sendJDQControl()
        }, this))
        envAirAutoCryogen.isSelected = false
        envAirAutoCryogen.setOnClickListener(ClickProxy(View.OnClickListener {
            if (!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirAutoCryogen, !envAirAutoCryogen.isSelected)
            isStartAutoCryogen = envAirAutoCryogen.isSelected
        }, this))
    }

    private fun changeStatusOfView(view: TextView, isSelected: Boolean) {
        view.isSelected = isSelected
        view.setTextColor(if (isSelected) RED else Color.BLACK)
    }

    private fun sendJDQControl() {
        when (currentJDQType) {
            JDQType.SCHIDERON -> {
                sendSerial(SerialCMD.JDQSearch)
            }
            JDQType.ZZIO1600 -> {
                sendSerial(SerialCMD.getZZSendControlCode(sendBean.switchIndex, sendBean.isOpen))
            }
        }
    }

    private fun sendJDQSearch() {
        when (currentJDQType) {
            JDQType.SCHIDERON -> {
                sendSerial(SerialCMD.JDQSearch)
            }
            JDQType.ZZIO1600 -> {
                sendSerial(SerialCMD.getZZSendSearchCode())
            }
        }
    }

    private fun sendSerial(msg: String) {
        var intent = Intent()
        intent.putExtra("data", "send")
        intent.putExtra("msg", msg)
        Logger.d("EmvAorActivity发送串口数据=$msg")
        intent.action = CommonConfig.SERIALPORTPROJECT_ACTION_SP_SERVICE
        sendBroadcast(intent)
    }


    private fun isCanClick(): Boolean {
        if (System.currentTimeMillis() - lastClickTime >= times) {
            lastClickTime = System.currentTimeMillis()
        } else {
            Toast.makeText(this, "您点得太快了", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0//环境检测仪获取到的数据
                -> {
                    Logger.d("获取的环境检测仪的数据" + msg.obj)
                    val enD = EnvironmentalDetector(msg.obj.toString())
                    Logger.i(
                        "---------经分析--------\n" +
                                "PM2.5=" + enD.pM2_5 + "\n" +
                                "温度=" + enD.temp + "\n" +
                                "湿度=" + enD.humidity + "\n" +
                                "CO2=" + enD.cO2 + "\n" +
                                "TVOC=" + enD.tvoc + "\n" +
                                "室外PM2.5=" + enD.pM2_5OutDoor + "\n" +
                                "甲醛=" + enD.formaldehyde + "\n" +
                                "-----------------------"
                    )
                    content.text = "当前温度：" + enD.temp + "C\t当前湿度：" + enD.humidity + "%RH\t" +
                            "当前TVOC：" + enD.tvoc + "mg/m3\t当前二氧化碳：" + enD.cO2 + "ppm"
                    if (isStartAutoCryogen) {//如果开启了自动制冷功能
                        if (enD.temp >= 24) {
                            if (envAirCryogen.isSelected) return
                            changeStatusOfView(envAirCryogen, true)
                            sendBean = SendBean().apply {
                                isSend = true
                                switchIndex = MODE_Cryogen
                                isOpen = true
                            }
                            sendJDQControl()
                        } else {
                            changeStatusOfView(envAirCryogen, false)
                            sendBean = SendBean().apply {
                                isSend = true
                                switchIndex = MODE_Cryogen
                                isOpen = false
                            }
                            sendJDQControl()
                        }
                    }
                }
                1 -> {
                    Logger.d("获取的SCHIDERON寄电器状态的数据" + msg.obj)
                    val data = msg.obj.toString().substring(16, 18)
                    if (sendBean != null && sendBean.isSend) {
                        //如果有控制命令，则讲获取的数据转为二进制，更改为要发送的二进制，在转成16进制发送
                        sendSerial(SerialCMD.getSendControlCode(calcuHexOfSendControl(data)))
                        //发送完成，关闭发送事件
                        sendBean.isSend = false
                    } else {
                        //如果不需要控制，只是查询状态，则将按钮与状态对应
                        var list = getStatus(data, 8)
                        changeStatusOfView(envAirCryogen, list[MODE_Cryogen] == 1)
                        changeStatusOfView(envAirHumidification, list[MODE_Humidification] == 1)
                        changeStatusOfView(envAirDehumidification, list[MODE_Dehumidification] == 1)
                        changeStatusOfView(envAirHairdryer, list[MODE_Hairdryer] == 1)
                        changeStatusOfView(envAirHeating, list[MODE_Heating] == 1)
                    }
                }
                2->{
                    Logger.d("获取的ZZ-IO1600寄电器状态的数据" + msg.obj)
                    val data = msg.obj.toString().substring(6,10)
                    var list=getStatus(data, 16)
                    //解析到的list含义前8位，代表的继电器9--16，后8位代表的继电器1--8
                    //例如[0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0]代表1/2/5/12继电器点亮
                   //这里我们从新组装哈数据的顺序，让1--16对应
                    var listNew=ArrayList<Int>()
                    for (i in 8 until 16){
                        listNew.add(list[i])
                    }
                    for (i in 0 until 8){
                        listNew.add(list[i])
                    }
                    changeStatusOfView(envAirCryogen, listNew[MODE_Cryogen] == 1)
                    changeStatusOfView(envAirHumidification, listNew[MODE_Humidification] == 1)
                    changeStatusOfView(envAirDehumidification, listNew[MODE_Dehumidification] == 1)
                    changeStatusOfView(envAirHairdryer, listNew[MODE_Hairdryer] == 1)
                    changeStatusOfView(envAirHeating, listNew[MODE_Heating] == 1)
                }
            }
        }
    }

    /**
     * 1.将获取到的寄电器开关状态转化为二进制
     * 2.根据我们需要的控制更改二进制
     * 3.将二进制转为发送需要的16进制
     */
    private fun calcuHexOfSendControl(data: String): String {
        var list = getStatus(data, 8)
        //设置点击模块的开关状态
        list[sendBean.switchIndex] = if (sendBean.isOpen) 1 else 0
        //设置点击模块关联模块的开关状态
        if (sendBean.isOpen) {
            when (sendBean.switchIndex) {
                //制冷和制热只有一个能处于启动中
                MODE_Cryogen -> {
                    list[MODE_Heating] = 0
                    changeStatusOfView(envAirHeating, false)
                }
                MODE_Heating -> {
                    list[MODE_Cryogen] = 0
                    changeStatusOfView(envAirCryogen, false)
                }
                //加湿和除湿只有一个能处于启动中
                MODE_Dehumidification -> {
                    list[MODE_Humidification] = 0
                    changeStatusOfView(envAirHumidification, false)
                }
                MODE_Humidification -> {
                    list[MODE_Dehumidification] = 0
                    changeStatusOfView(envAirDehumidification, false)
                }
            }
        }
        var bin = ""
        for (i in 0 until list.size) {
            //在获取开关状态的时候，从二进制取0和1，最低位先取，所以转成二进制时，要反着拿值
            bin += list[list.size - 1 - i]
        }
        var hex = intToHex(Integer.parseInt(bin, 2))
        return hex
    }

    /**
     * 10进制转16进制
     */
    private fun intToHex(n: Int): String {
        var n = n
        var s = StringBuffer()
        var a: String
        val b = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        if (n == 0) return "00"
        while (n != 0) {
            s = s.append(b[n % 16])
            n /= 16
        }
        a = s.reverse().toString()
        if (a.length == 1) a = "0$a"
        return a
    }

    /**
     * 获取寄电器开关的状态数据
     */
    private fun getStatus(data: String, len: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        for (i in 0 until len) {
            list.add(Integer.parseInt(data, 16) shr i and 1)
        }
        return list
    }

    internal inner class EnvAirBoradCast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI) {
                val data = intent.getStringExtra("msg")
                buffer.append(data)
                if (buffer.toString().length > 4 && buffer.toString().substring(0, 4) == "0103") {//环境探测器数据
                    val message = Message.obtain()
                    message.what = 0
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 4 &&
                    buffer.toString().substring(0, 4).toLowerCase() == "AA55".toLowerCase() &&
                    buffer.toString().substring(buffer.length - 4, buffer.length).toLowerCase() == "0D0A".toLowerCase()
                ) {//SCHIDERON寄电器状态
                    val message = Message.obtain()
                    message.what = 1
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 6 &&
                    buffer.toString().substring(0, 6).toLowerCase() == "FE0102".toLowerCase()
                ) {//ZZ-IO1600寄电器状态
                    val message = Message.obtain()
                    message.what = 2
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
            }
        }
    }
}