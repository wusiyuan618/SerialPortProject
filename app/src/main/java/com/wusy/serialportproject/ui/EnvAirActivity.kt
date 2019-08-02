package com.wusy.serialportproject.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.RED
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.bean.SendBean
import com.wusy.serialportproject.proxy.ClickProxy
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.serialportproject.util.SerialCMD
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.util.LoadingViewUtil
import kotlinx.android.synthetic.main.activity_env_air.*
import java.util.ArrayList


class EnvAirActivity : BaseActivity() {
    companion object {
        const val Cryogen = 0
        const val Heating = 1
        const val Dehumidification = 2
        const val Humidification = 3
        const val Hairdryer = 4
    }

    private var loadingDialog: Dialog? = null
    private var boradCast: EnvAirBoradCast? = null
    private val buffer = StringBuffer()
    private var sendBean: SendBean = SendBean()
    private var isStartAutoCryogen = false

    private var lastClickTime:Long = 0
    private val times: Long = 1000
    override fun getContentViewId(): Int {
        return R.layout.activity_env_air
    }


    override fun findView() {
    }

    override fun init() {
        loadingDialog = LoadingViewUtil.getInstance().createLoadingDialog(this, "请稍后")
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
                sendSerial(SerialCMD.JDQSearch)
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
            if(!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirCryogen, !envAirCryogen.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = Cryogen
                isOpen = envAirCryogen.isSelected
            }
            sendSerial(SerialCMD.JDQSearch)
        }, this))
        envAirHeating.setOnClickListener(ClickProxy(View.OnClickListener {
            if(!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirHeating, !envAirHeating.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = Heating
                isOpen = envAirHeating.isSelected

            }
            sendSerial(SerialCMD.JDQSearch)
        }, this))
        envAirDehumidification.setOnClickListener(ClickProxy(View.OnClickListener {
            if(!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirDehumidification, !envAirDehumidification.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = Dehumidification
                isOpen = envAirDehumidification.isSelected

            }
            sendSerial(SerialCMD.JDQSearch)
        }, this))
        envAirHumidification.setOnClickListener(ClickProxy(View.OnClickListener {
            if(!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirHumidification, !envAirHumidification.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = Humidification
                isOpen = envAirHumidification.isSelected

            }
            sendSerial(SerialCMD.JDQSearch)
        }, this))
        envAirHairdryer.setOnClickListener(ClickProxy(View.OnClickListener {
            if(!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirHairdryer, !envAirHairdryer.isSelected)
            sendBean = SendBean().apply {
                isSend = true
                switchIndex = Hairdryer
                isOpen = envAirHairdryer.isSelected

            }
            sendSerial(SerialCMD.JDQSearch)
        }, this))
        envAirAutoCryogen.isSelected = false
        envAirAutoCryogen.setOnClickListener(ClickProxy(View.OnClickListener {
            if(!isCanClick()) return@OnClickListener
            changeStatusOfView(envAirAutoCryogen, !envAirAutoCryogen.isSelected)
            isStartAutoCryogen = envAirAutoCryogen.isSelected
        }, this))
    }

    private fun changeStatusOfView(view: TextView, isSelected: Boolean) {
        view.isSelected = isSelected
        view.setTextColor(if (isSelected) RED else Color.BLACK)
    }

    private fun sendSerial(msg: String) {
        var intent = Intent()
        intent.putExtra("data", "send")
        intent.putExtra("msg", msg)
        Logger.i("send serial broadcat msg=$msg")
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
                    Logger.i("获取的环境检测仪的数据" + msg.obj)
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
                        if (enD.temp >= 24 ) {
                            if(envAirCryogen.isSelected)return
                            changeStatusOfView(envAirCryogen, true)
                            sendBean = SendBean().apply {
                                isSend = true
                                switchIndex = Cryogen
                                isOpen = true
                            }
                            sendSerial(SerialCMD.JDQSearch)
                        } else {
                            changeStatusOfView(envAirCryogen, false)
                            sendBean = SendBean().apply {
                                isSend = true
                                switchIndex = Cryogen
                                isOpen = false
                            }
                            sendSerial(SerialCMD.JDQSearch)
                        }
                    }
                }
                1 -> {
                    Logger.i("获取的寄电器状态的数据" + msg.obj)
                    val data = msg.obj.toString().substring(16, 18)
                    if (sendBean != null && sendBean.isSend) {
                        //如果有控制命令，则讲获取的数据转为二进制，更改为要发送的二进制，在转成16进制发送
                        sendSerial(SerialCMD.getSendControlCode(calcuHexOfSendControl(data)))
                        //发送完成，关闭发送事件
                        sendBean.isSend = false
                    } else {
                        //如果不需要控制，只是查询状态，则将按钮与状态对应
                        var list = getStatus(data, 8)
                        changeStatusOfView(envAirCryogen, list[Cryogen] == 1)
                        changeStatusOfView(envAirHumidification, list[Humidification] == 1)
                        changeStatusOfView(envAirDehumidification, list[Dehumidification] == 1)
                        changeStatusOfView(envAirHairdryer, list[Hairdryer] == 1)
                        changeStatusOfView(envAirHeating, list[Heating] == 1)
                    }
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
        list[sendBean.switchIndex] = if (sendBean.isOpen) 1 else 0
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
                Logger.i("get SerialPort msg by BroadCast-HJL_ACTION_SERIALPORT_MSG=$data")

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
                ) {//寄电器状态
                    val message = Message.obtain()
                    message.what = 1
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
            }
        }
    }
}