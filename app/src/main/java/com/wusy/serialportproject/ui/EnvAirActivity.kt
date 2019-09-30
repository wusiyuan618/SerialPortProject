package com.wusy.serialportproject.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color

import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.adapter.EnvAirAdapter
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.bean.EnvAirControlBean
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.serialportproject.util.JDQType
import com.wusy.serialportproject.util.SerialCMD
import com.wusy.serialportproject.view.CirqueProgressControlView
import com.wusy.wusylibrary.base.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_envair.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min


class EnvAirActivity : BaseTouchActivity() {
    /**
     *  对应寄电器开关的位置（即第N个开关）
     */
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
         * 新风
         */
        const val MODE_Hairdryer = 11

        /**
         * 当前使用的继电器
         */
        const val currentJDQType = JDQType.ZZIO1600
    }


    private var boradCast: EnvAirBoradCast? = null
    private val buffer = StringBuffer()
    private var sendBean: EnvAirControlBean = EnvAirControlBean()
    //是否开启自动制冷
    private var isStartAutoCryogen = false
    private var adapter: EnvAirAdapter? = null
    private var lastClickTime: Long = 0
    //点击事件间隔时间
    private val times: Long = 1000
    private var curTemp=18
    private val minTemp=16
    private val maxTemp=31
    override fun getContentViewId(): Int {
        return R.layout.activity_envair
    }


    override fun findView() {
    }

    override fun init() {
        initView()
        initBroadCast()
        initThread()

    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        initControlRecycler()
        tempControlView.setProgressRange(16, 32)//可以在xml中指定，也可以在代码中设置
        tempControlView.setProgress(27)  //添加默认数据--注:不能超出范围
        tempControlView.setOnTextFinishListener(object : CirqueProgressControlView.OnCirqueProgressChangeListener{
            override fun onChange(minProgress: Int, maxProgress: Int, progress: Int) {
                Log.i("wsy",progress.toString() + "")
            }

            override fun onChangeEnd(minProgress: Int, maxProgress: Int, progress: Int) {
                Log.i("wsy", "control finish. last progress is $progress")
            }
        })
        tvON.setOnClickListener {
            tvON.setTextColor(Color.parseColor("#2793FF"))
            tvOFF.setTextColor(Color.parseColor("#FFFFFF"))

        }
        tvOFF.setOnClickListener {
            tvON.setTextColor(Color.parseColor("#FFFFFF"))
            tvOFF.setTextColor(Color.parseColor("#2793FF"))
        }
        rlSetting.setOnClickListener {
            navigateTo(SettingActivity::class.java)
        }
        rlRepair.setOnClickListener {
            navigateTo(RepairActivity::class.java)
        }
    }

    private fun initControlRecycler() {
        rvControl.layoutManager = GridLayoutManager(this, 1) as RecyclerView.LayoutManager?
        adapter = EnvAirAdapter(this)
        var list = ArrayList<EnvAirControlBean>()
        list.add(EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.type = 0
            this.switchIndex = MODE_Cryogen
            this.content = "制冷/Cool"
            this.imgResourceSelect = R.mipmap.icon_cool_selected
            this.imgResourceNormal = R.mipmap.icon_cool_normal
        })
        list.add(EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.type = 1
            this.switchIndex = MODE_Heating
            this.content = "制热/Heat"
            this.imgResourceSelect = R.mipmap.icon_heat_selected
            this.imgResourceNormal = R.mipmap.icon_heat_normal
        })
        list.add(EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.type = 2
            this.switchIndex = MODE_Hairdryer
            this.content = "新风/Fresh"
            this.imgResourceSelect = R.mipmap.icon_fresh_selected
            this.imgResourceNormal = R.mipmap.icon_fresh_normal
        })
        adapter?.let {
            it.list = list
            it.setOnRecyclerItemClickLitener(object : BaseRecyclerAdapter.onRecyclerItemClickLitener {
                override fun onRecyclerItemLongClick(view: RecyclerView.ViewHolder?, position: Int) {}
                override fun onRecyclerItemClick(view: RecyclerView.ViewHolder?, position: Int) {
                    if (!isCanClick()) return
                    it.list[position].isSend = true
                    it.list[position].isOpen = !it.list[position].isOpen
                    sendBean = it.list[position]
                    sendJDQControl()
                    it.notifyDataSetChanged()
                }
            })
            rvControl.adapter = adapter
        }

    }

    private fun initThread() {
        Thread(Runnable {
            //这是一个每1min执行一次的定时器
            while (true) {
                buffer.delete(0, buffer.length)//定时更新下数据存储器，防止出现骚问题
                sendSerial(SerialCMD.EnvironmenttalSearch)
                Thread.sleep(1000)
                sendJDQSearch()
                Thread.sleep(60 * 1000)
            }
        }).start()
        Thread(Runnable {
            while(true){
                Thread.sleep(1000)
                runOnUiThread {
                    var calendar= Calendar.getInstance()
                    tvDate.text = calendar.get(Calendar.YEAR).toString() + "年" +
                            (calendar.get(Calendar.MONTH) + 1).toString() + "月" +
                            calendar.get(Calendar.DAY_OF_MONTH).toString() + "日" + "      周" + calendar.get(Calendar.DAY_OF_WEEK)
                    tvTime.text=calendar.get(Calendar.HOUR_OF_DAY).toString()+" : "+calendar.get(Calendar.MINUTE).toString()
                }

            }
        }).start()
    }

    private fun initBroadCast() {
        boradCast = EnvAirBoradCast()
        var actionList = ArrayList<String>()
        actionList.add(CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI)
        addBroadcastAction(actionList, boradCast)
    }


    private fun changeStatusOfView(type: Int, isOpen: Boolean) {
        var bean = searchControlBean(type)
        if (bean != null) {
            bean.isOpen = isOpen
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun searchControlBean(type: Int): EnvAirControlBean? {
        adapter?.let {
            for (bean in it.list) {
                if (bean.type == type) {
                    return bean
                }
            }
        }
        return null
    }

    /**
     * 发送寄电器控制命令
     */
    private fun sendJDQControl() {
        when (currentJDQType) {
            JDQType.SCHIDERON -> {
                sendSerial(SerialCMD.JDQSearch)
            }
            JDQType.ZZIO1600 -> {
                if (sendBean.isOpen) {//确实是打开命令
                    when (sendBean.type) {//先解决冲突开关，例如制热和制冷。不允许同时打开
                        EnvAirControlBean.TYPE_Cryogen -> {//发送制冷命令
                            var heatingBean = searchControlBean(EnvAirControlBean.TYPE_Heating)
                            if (heatingBean != null && heatingBean.isOpen) {
                                sendSerial(SerialCMD.getZZSendControlCode(heatingBean.switchIndex, false))
                                changeStatusOfView(EnvAirControlBean.TYPE_Heating, false)
                            }
                        }
                        EnvAirControlBean.TYPE_Heating -> {//发送制热命令
                            var cryogenBean = searchControlBean(EnvAirControlBean.TYPE_Cryogen)
                            if (cryogenBean != null && cryogenBean.isOpen) {
                                sendSerial(SerialCMD.getZZSendControlCode(cryogenBean.switchIndex, false))
                                changeStatusOfView(EnvAirControlBean.TYPE_Cryogen, false)
                            }
                        }
                    }
                }
                Thread(Runnable {
                    Thread.sleep(100)
                    sendSerial(SerialCMD.getZZSendControlCode(sendBean.switchIndex, sendBean.isOpen))
                }).start()
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
                    //将确定是环境探测器的数据通过广播发出去,并且存储全局数据。方便屏保使用
                    var intent=Intent(CommonConfig.ACTION_ENVIRONMENTALDETECOTOR_DATA)
                    intent.putExtra("data",msg.obj.toString())
                    sendBroadcast(intent)
                    val enD = EnvironmentalDetector(msg.obj.toString())
                    Constants.curED=enD
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
                    tvTempCount.text = enD.temp.toString()
                    tvHumidityCount.text = enD.humidity.toString()
                    var cryogenControl = searchControlBean(EnvAirControlBean.TYPE_Cryogen)
                    if (isStartAutoCryogen) {//如果开启了自动制冷功能
                        if (cryogenControl != null) {
                            if (enD.temp >= curTemp) {
                                if (cryogenControl.isOpen) return
                                cryogenControl.isOpen = true
                                cryogenControl.isSend = true
                                sendBean = cryogenControl
                                sendJDQControl()
                            } else {
                                changeStatusOfView(EnvAirControlBean.TYPE_Cryogen, false)
                                cryogenControl.isOpen = false
                                cryogenControl.isSend = true
                                sendBean = cryogenControl
                                sendJDQControl()
                            }
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
                        var statusList = getStatus(data, 8)
                        adapter?.let {
                            for (bean in it.list) {
                                bean.isOpen = statusList[bean.switchIndex] == 1
                            }
                            it.notifyDataSetChanged()
                        }
                    }
                }
                2 -> {
                    Logger.d("获取的ZZ-IO1600寄电器状态的数据" + msg.obj)
                    val data = msg.obj.toString().substring(6, 10)
                    var list = getStatus(data, 16)
                    //解析到的list含义前8位，代表的继电器9--16，后8位代表的继电器1--8
                    //例如[0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0]代表1/2/5/12继电器点亮
                    //这里我们从新组装哈数据的顺序，让1--16对应
                    var listNew = ArrayList<Int>()
                    for (i in 8 until 16) {
                        listNew.add(list[i])
                    }
                    for (i in 0 until 8) {
                        listNew.add(list[i])
                    }
                    adapter?.let {
                        for (bean in it.list) {
                            bean.isOpen = listNew[bean.switchIndex] == 1
                        }
                        it.notifyDataSetChanged()
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
        //设置点击模块的开关状态
        list[sendBean.switchIndex] = if (sendBean.isOpen) 1 else 0
        //设置点击模块关联模块的开关状态
        if (sendBean.isOpen) {
            when (sendBean.switchIndex) {
                //制冷和制热只有一个能处于启动中
                MODE_Cryogen -> {
                    list[MODE_Heating] = 0
                    changeStatusOfView(EnvAirControlBean.TYPE_Heating, false)
                }
                MODE_Heating -> {
                    list[MODE_Cryogen] = 0
                    changeStatusOfView(EnvAirControlBean.TYPE_Cryogen, false)
                }
                //加湿和除湿只有一个能处于启动中
                MODE_Dehumidification -> {
                    list[MODE_Humidification] = 0
                    changeStatusOfView(EnvAirControlBean.TYPE_Humidification, false)
                }
                MODE_Humidification -> {
                    list[MODE_Dehumidification] = 0
                    changeStatusOfView(EnvAirControlBean.TYPE_Dehumidification, false)
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