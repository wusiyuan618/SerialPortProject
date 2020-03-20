package com.wusy.serialportproject.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.adapter.EnvAirAdapter
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.EnvironmentalDetector
import com.wusy.serialportproject.bean.EnvAirControlBean
import com.wusy.serialportproject.devices.BaseDevices
import com.wusy.serialportproject.devices.EnvQ3
import com.wusy.serialportproject.devices.SCHIDERON
import com.wusy.serialportproject.devices.ZZIO1600
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.serialportproject.util.JDQType
import com.wusy.serialportproject.view.CirqueProgressControlView
import com.wusy.wusylibrary.base.BaseRecyclerAdapter
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_envair.*
import kotlinx.android.synthetic.main.activity_envair.tvTime
import kotlinx.android.synthetic.main.activity_screen_clock.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EnvAirActivity : BaseTouchActivity() {
    /**
     *  对应寄电器开关的位置（即第N个开关）
     */
    companion object {
        /**
         * 开启
         */
        const val MODE_ON = 0
        /**
         * 关闭
         */
        const val MODE_OFF = 1
        /**
         * 制冷
         */
        const val MODE_Cryogen = 2
        /**
         * 制热
         */
        const val MODE_Heating = 3
        /**
         * 除湿
         */
        const val MODE_Dehumidification = 4
        /**
         * 加湿
         */
        const val MODE_Humidification = 5
        /**
         * 新风
         */
        const val MODE_Hairdryer = 6

        /**
         * 当前使用的继电器
         */
        val currentJDQ: BaseDevices = ZZIO1600()
        val currentEnv: BaseDevices = EnvQ3()
    }


    private var boradCast: EnvAirBoradCast? = null
    private val buffer = StringBuffer()
    /**
     * 最后发送的开关控制实体
     */
    private var sendBean: EnvAirControlBean = EnvAirControlBean()
    private var lastClickTime: Long = 0
    //点击事件间隔时间
    private val times: Long = 500
    private var curTemp = 25
    private val minTemp = 16
    private val maxTemp = 32
    //自动制冷
    private var isCryogen = false
    //自动制热
    private var isHeating = false
    //新风定时按钮是否打开
    private var isXFTime = false
    private var nextTime = 0L

    /**
     * 按钮实体
     */
    lateinit var btnClodBean: EnvAirControlBean
    lateinit var btnHeatBean: EnvAirControlBean
    lateinit var btnJSBean: EnvAirControlBean
    lateinit var btnCSBean: EnvAirControlBean
    lateinit var btnXFBean: EnvAirControlBean
    lateinit var btnOnBean: EnvAirControlBean
    lateinit var btnOffBean: EnvAirControlBean

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
        initAllBtn()
        tempControlView.setProgressRange(minTemp, maxTemp)//可以在xml中指定，也可以在代码中设置
        curTemp = SharedPreferencesUtil.getInstance(this).getData(Constants.ENJOYTEMP, 25) as Int
        tempControlView.setProgress(curTemp)  //添加默认数据--注:不能超出范围
        tempControlView.setOnTextFinishListener(object :
            CirqueProgressControlView.OnCirqueProgressChangeListener {
            override fun onChange(minProgress: Int, maxProgress: Int, progress: Int) {
//                Log.i("wsy", progress.toString() + "")
            }

            override fun onChangeEnd(minProgress: Int, maxProgress: Int, progress: Int) {
                curTemp = progress
                SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                    .saveData(Constants.ENJOYTEMP, curTemp)
            }
        })
        rlSetting.setOnClickListener {
            navigateTo(SettingActivity::class.java)
        }
        rlRepair.setOnClickListener {
            navigateTo(RepairActivity::class.java)
        }
//        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_LN, 0)) {
//            0 -> {
//                ivLNOFF.setImageResource(R.mipmap.btn_close_selected)
//                isCryogen = false
//                isHeating = false
//            }
//            1 -> {
//                ivHeat.setImageResource(R.mipmap.btn_hot_selected)
//                isCryogen = false
//                isHeating = true
//            }
//            2 -> {
//                ivClod.setImageResource(R.mipmap.btn_cool_selected)
//                isCryogen = true
//                isHeating = false
//            }
//        }
        isXFTime = SharedPreferencesUtil.getInstance(this).getData(
            Constants.ISOPEN_XFTIME,
            false
        ) as Boolean
        if (isXFTime) controlXFTime(true)
    }

    private fun initThread() {
        Thread(Runnable {
            //这是一个每1min执行一次的定时器，用于检测寄电器状态和环境状态
            while (true) {
                if (isXFTime) {//新风定时功能启动中
                    var calendar = Calendar.getInstance()
                    if (SimpleDateFormat("hh:mm").format(Date(nextTime)) == SimpleDateFormat("hh:mm").format(
                            Date(calendar.timeInMillis)
                        )
                    ) {
                        if (btnXFBean.isOpen) {
                            controlXFTime(false)
                        } else {
                            controlXFTime(true)
                        }
                    }
                }
                Thread.sleep(1000)
                buffer.delete(0, buffer.length)//定时更新下数据存储器，防止出现骚问题
                sendSerial(currentEnv.SearchStatusCode)
                Thread.sleep(1000)
                sendJDQSearch()
                Thread.sleep(58 * 1000)
            }
        }).start()
        Thread(Runnable {
            //这是一个每分钟执行一次的时间线程
            while (true) {
                var calendar = Calendar.getInstance()
                runOnUiThread {
                    tvDate.text = calendar.get(Calendar.YEAR).toString() + "年" +
                            (calendar.get(Calendar.MONTH) + 1).toString() + "月" +
                            calendar.get(Calendar.DAY_OF_MONTH).toString() + "日" + "      周" + calendar.get(
                        Calendar.DAY_OF_WEEK
                    )
                    tvTime.text = SimpleDateFormat("hh:mm").format(calendar.time)
                }
                Thread.sleep(1000)
            }
        }).start()
    }

    private fun initBroadCast() {
        boradCast = EnvAirBoradCast()
        var actionList = ArrayList<String>()
        actionList.add(CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI)
        addBroadcastAction(actionList, boradCast)
    }


    /**
     * 发送寄电器控制命令
     */
    private fun sendJDQControl() {
        Thread.sleep(100)
        when (currentJDQ.name) {
            JDQType.SCHIDERON -> {
                sendSerial(currentJDQ.SearchStatusCode)
            }
            JDQType.ZZIO1600 -> {
                sendSerial(
                    (currentJDQ as ZZIO1600).getSendControlCode(
                        sendBean.switchIndex,
                        sendBean.isOpen
                    )
                )
                Logger.i("发送了一条寄电器控制命令：$sendBean")
            }
        }
    }

    /**
     * 查询寄电器状态
     */
    private fun sendJDQSearch() {
        sendSerial(currentJDQ.SearchStatusCode)
    }

    /**
     * 通过串口发送命令
     */
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

    private fun isCanClickByOpen(): Boolean {
        return if (btnOnBean.isOpen) {
            true
        } else {
            Toast.makeText(this, "请启动空调", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 3 -> {//环境检测仪获取到的数据
                    Logger.d("获取的环境检测仪的数据" + msg.obj)
                    //将确定是环境探测器的数据通过广播发出去,并且存储全局数据。方便屏保使用
                    var intent = Intent(CommonConfig.ACTION_ENVIRONMENTALDETECOTOR_DATA)
                    intent.putExtra("data", msg.obj.toString())
                    sendBroadcast(intent)
                    val enD = EnvironmentalDetector(msg.obj.toString(), currentEnv)
                    Constants.curED = enD
                    tvTempCount.text = enD.temp.toString()
                    tvHumidityCount.text = enD.humidity.toString()
                    /* 开启制冷制热 */
                    startAutoCryogen(enD)
                    startAutoHeating(enD)
                }
                1 -> {
                    Logger.d("获取的SCHIDERON寄电器状态的数据" + msg.obj)
                    val data = msg.obj.toString().substring(16, 18)
                    if (sendBean != null && sendBean.isSend) {
                        //如果有控制命令，则讲获取的数据转为二进制，更改为要发送的二进制，在转成16进制发送
                        sendSerial(
                            (currentJDQ as SCHIDERON).getSendControlCode(
                                calcuHexOfSendControl(data)
                            )
                        )
                        //发送完成，关闭发送事件
                        sendBean.isSend = false
                    } else {
                        //如果不需要控制，只是查询状态，则将按钮与状态对应

                    }
                }
                2 -> {
                    Logger.d("获取的ZZ-IO1600寄电器状态的数据" + msg.obj)
                    var praseList = (currentJDQ as ZZIO1600).parseStatusData(msg.obj.toString())

                    /**
                     * 冷暖开关状态检查(逻辑更改，暂时不用)
                     */
//                    ivClod.setImageResource(R.mipmap.btn_cool_normal)
//                    ivHeat.setImageResource(R.mipmap.btn_hot_normal)
//                    ivLNOFF.setImageResource(R.mipmap.btn_close_normal)
//                    if(praseList[btnClodBean.switchIndex]==1){//制冷中
//                        ivClod.setImageResource(R.mipmap.btn_cool_selected)
//                        btnClodBean.isOpen=true
//                    }else if(praseList[btnHeatBean.switchIndex]==1){//制热中
//                        ivHeat.setImageResource(R.mipmap.btn_hot_selected)
//                        btnHeatBean.isOpen=true
//                    }else{//都关闭
//                        ivLNOFF.setImageResource(R.mipmap.btn_close_selected)
//                    }
                    /**
                     * 湿度开关状态检查
                     */
                    ivJS.setImageResource(R.mipmap.btn_humidification_normal)
                    ivCS.setImageResource(R.mipmap.btn_dehumidification_normal)
                    ivSDOFF.setImageResource(R.mipmap.btn_close_normal)
                    if (praseList[btnJSBean.switchIndex] == 1) {//加湿中
                        ivJS.setImageResource(R.mipmap.btn_humidification_selected)
                        btnJSBean.isOpen = true
                    } else if (praseList[btnCSBean.switchIndex] == 1) {//除湿中
                        ivCS.setImageResource(R.mipmap.btn_dehumidification_selected)
                        btnCSBean.isOpen = true
                    } else {//都关闭
                        ivSDOFF.setImageResource(R.mipmap.btn_close_selected)
                    }
                    /**
                     * 新风开关状态检查
                     */
                    ivXFOFF.setImageResource(R.mipmap.btn_close_normal)
                    ivXFON.setImageResource(R.mipmap.btn_open_normal)
                    ivTime.setImageResource(R.mipmap.btn_ontime_normal)
                    if (isXFTime) {
                        ivTime.setImageResource(R.mipmap.btn_ontime_selected)
                        btnXFBean.isOpen = praseList[btnXFBean.switchIndex] == 1
                    } else {
                        if (praseList[btnXFBean.switchIndex] == 1) {//新风开启中
                            ivXFON.setImageResource(R.mipmap.btn_open_selected)
                            btnXFBean.isOpen = true
                        } else {
                            ivXFOFF.setImageResource(R.mipmap.btn_close_selected)
                            btnXFBean.isOpen = false
                        }
                    }
                }
            }
        }
    }

    /**
     * 自动制冷
     */
    private fun startAutoCryogen(enD: EnvironmentalDetector) {
        if (isCryogen) {
            if (enD.temp > curTemp + 1) {//温度高了，发送打开制冷
                if (btnClodBean.isOpen) return
                Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送打开制冷命令")
                btnClodBean.isOpen = true
                btnClodBean.isSend = true
                sendBean = btnClodBean
                sendJDQControl()
            } else if (enD.temp < curTemp - 1) {
                if (btnClodBean.isOpen) {//温度低了，发送关闭制冷
                    Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送关闭制冷命令")
                    btnClodBean.isOpen = false
                    btnClodBean.isSend = true
                    sendBean = btnClodBean
                    sendJDQControl()
                }
            } else {
                Logger.i("适宜温度，不需要操作制冷命令")
            }
        }
    }

    /**
     * 自动制热
     */
    private fun startAutoHeating(enD: EnvironmentalDetector) {
        if (isHeating) {
            if (enD.temp < curTemp - 1) {//温度低了，发送打开制热
                Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送打开制热命令")
                if (btnHeatBean.isOpen) return
                btnHeatBean.isOpen = true
                btnHeatBean.isSend = true
                sendBean = btnHeatBean
                sendJDQControl()
            } else if (enD.temp > curTemp + 1) {
                if (btnHeatBean.isOpen) {//温度高，发送关闭制热
                    Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送关闭制热命令")
                    btnHeatBean.isOpen = false
                    btnHeatBean.isSend = true
                    sendBean = btnHeatBean
                    sendJDQControl()
                }
            } else {
                Logger.i("适宜温度，不需要操作制热命令")
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
                }
                MODE_Heating -> {
                    list[MODE_Cryogen] = 0
                }
                //加湿和除湿只有一个能处于启动中
                MODE_Dehumidification -> {
                    list[MODE_Humidification] = 0
                }
                MODE_Humidification -> {
                    list[MODE_Dehumidification] = 0
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
        val b = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )
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

    /**
     * -------------------------------按钮处理----------------------------------------
     */
    /**
     * 初始化所有按钮
     */
    private fun initAllBtn() {
        /**
         * 制热
         */
        btnHeatBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_Heating
            this.content = "制热"
        }
        llHeat.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            recordingBtnState("ln", 1)
            ZR()
        }
        /**
         * 制冷
         */
        btnClodBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_Cryogen
            this.content = "制冷"
        }
        llClod.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            recordingBtnState("ln", 2)
            ZL()
        }
        /**
         * 冷暖全关
         */
        llLNOFF.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            Logger.i("正在冷暖全关")
            recordingBtnState("ln", 0)
            LNAllOff()
        }
        /**
         * 加湿
         */
        btnJSBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_Humidification
            this.content = "加湿"
        }
        llJS.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            recordingBtnState("sd", 2)
            JS()
        }
        /**
         * 除湿
         */
        btnCSBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_Dehumidification
            this.content = "除湿"
        }
        llCS.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            recordingBtnState("sd", 1)
            CS()
        }
        /**
         * 湿度全关
         */
        llSDOFF.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            Logger.i("正在打开湿度全关功能")
            recordingBtnState("sd", 0)
            SDAllOff()
        }
        btnOnBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_ON
            this.content = "开启"
        }
        btnOffBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_OFF
            this.content = "关闭"
        }
        /**
         * 开启按钮
         */
        llON.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            Logger.i("空调已启动")
            btnOffBean.isOpen = false
            btnOffBean.isSend = true
            sendBean = btnOffBean
            sendJDQControl()
            btnOnBean.isSend = true
            btnOnBean.isOpen = true
            sendBean = btnOnBean
            sendJDQControl()
            restoreBtnState()
            ivON.setImageResource(R.mipmap.btn_on_selected)
            ivLJ.setImageResource(R.mipmap.btn_outhome_normal)
            ivJN.setImageResource(R.mipmap.btn_energy_normal)
            ivOFF.setImageResource(R.mipmap.btn_off_normal)

        }
        /**
         * 关闭按钮
         */
        llOFF.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            Logger.i("空调已关闭")
            LNAllOff()
            SDAllOff()
            XFAllOff()
            if (btnOnBean.isOpen) {
                btnOnBean.isOpen = false
                btnOnBean.isSend = true
                sendBean = btnOnBean
                sendJDQControl()
                btnOffBean.isSend = true
                btnOffBean.isOpen = true
                sendBean = btnOffBean
                sendJDQControl()
            } else {
                btnOffBean.isOpen = true
                btnOffBean.isSend = true
                sendBean = btnOffBean
                sendJDQControl()
            }
            ivON.setImageResource(R.mipmap.btn_on_normal)
            ivLJ.setImageResource(R.mipmap.btn_outhome_normal)
            ivJN.setImageResource(R.mipmap.btn_energy_normal)
            ivOFF.setImageResource(R.mipmap.btn_off_selected)
        }
        /**
         * 离家按钮
         */
        llLJ.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (isHeating) {
                curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                    Constants.DEFAULT_TEMP_OUTHOME_ZR,
                    Constants.DEFAULT_TEMPDATA_LJZR
                ) as
                        String).split("℃")[0].toInt()
                tempControlView.setProgress(curTemp)
            } else if (isCryogen) {
                curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                    Constants.DEFAULT_TEMP_OUTHOME_ZL,
                    Constants.DEFAULT_TEMPDATA_LJZL
                ) as String).split("℃")[0].toInt()
                tempControlView.setProgress(curTemp)
            } else {
                showToast("请先选择冷暖模式")
                return@setOnClickListener
            }
            Logger.i("离家模式启动")

            btnOffBean.isOpen = false
            btnOffBean.isSend = true
            sendBean = btnOffBean
            sendJDQControl()
            btnOnBean.isSend = true
            btnOnBean.isOpen = true
            sendBean = btnOnBean
            sendJDQControl()

            ivON.setImageResource(R.mipmap.btn_on_normal)
            ivLJ.setImageResource(R.mipmap.btn_outhome_selected)
            ivJN.setImageResource(R.mipmap.btn_energy_normal)
            ivOFF.setImageResource(R.mipmap.btn_off_normal)
        }
        /**
         * 节能按钮
         */
        llJN.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (isHeating) {
                curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                    Constants.DEFAULT_TEMP_JN_ZR,
                    Constants.DEFAULT_TEMPDATA_JNZR
                ) as String).split("℃")[0].toInt()
                tempControlView.setProgress(curTemp)
            } else if (isCryogen) {
                curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                    Constants.DEFAULT_TEMP_JN_ZL,
                    Constants.DEFAULT_TEMPDATA_JNZL
                ) as String).split("℃")[0].toInt()
                tempControlView.setProgress(curTemp)
            } else {
                showToast("请先选择冷暖模式")
                return@setOnClickListener
            }
            Logger.i("节能模式启动")
            btnOffBean.isOpen = false
            btnOffBean.isSend = true
            sendBean = btnOffBean
            sendJDQControl()
            btnOnBean.isSend = true
            btnOnBean.isOpen = true
            sendBean = btnOnBean
            sendJDQControl()
            ivON.setImageResource(R.mipmap.btn_on_normal)
            ivLJ.setImageResource(R.mipmap.btn_outhome_normal)
            ivJN.setImageResource(R.mipmap.btn_energy_selected)
            ivOFF.setImageResource(R.mipmap.btn_off_normal)
        }
        /**
         * 新风
         */
        btnXFBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.isSend = false
            this.switchIndex = MODE_Hairdryer
            this.content = "新风"
        }
        /**
         * 新风开启按钮
         */
        llXFON.setOnClickListener {
            if (!isCanClickByOpen()) return@setOnClickListener
            if (!isCanClick()) return@setOnClickListener
            recordingBtnState("xf", 2)
            XFON()
        }
        /**
         * 新风关闭按钮
         */
        llXFOFF.setOnClickListener {
            Logger.i("正在关闭新风功能")
            recordingBtnState("xf", 1)
            XFAllOff()
        }
        /**
         * 新风定时按钮
         */
        llTime.setOnClickListener {
            if (!isCanClickByOpen()) return@setOnClickListener
            if (!isCanClick()) return@setOnClickListener
            recordingBtnState("xf", 0)
            XFTime()
        }
    }

    private fun ZR() {
        Logger.i("正在打开制热功能")
        isHeating = true
        isCryogen = false
        btnClodBean.isOpen = false
        btnClodBean.isSend = true
        sendBean = btnClodBean
        sendJDQControl()
        btnHeatBean.isOpen = true
        btnHeatBean.isSend = true
        sendBean = btnHeatBean
        sendJDQControl()
        ivClod.setImageResource(R.mipmap.btn_cool_normal)
        ivHeat.setImageResource(R.mipmap.btn_hot_selected)
        ivLNOFF.setImageResource(R.mipmap.btn_close_normal)
    }

    private fun ZL() {
        Logger.i("正在打开制冷功能")
        isHeating = false
        isCryogen = true
        btnHeatBean.isOpen = false
        btnHeatBean.isSend = true
        sendBean = btnHeatBean
        sendJDQControl()
        btnClodBean.isSend = true
        btnClodBean.isOpen = true
        sendBean = btnClodBean
        sendJDQControl()
        ivClod.setImageResource(R.mipmap.btn_cool_selected)
        ivHeat.setImageResource(R.mipmap.btn_hot_normal)
        ivLNOFF.setImageResource(R.mipmap.btn_close_normal)
    }

    private fun JS() {
        Logger.i("正在打开加湿功能")
        btnCSBean.isOpen = false
        btnCSBean.isSend = true
        sendBean = btnCSBean
        sendJDQControl()
        btnJSBean.isOpen = true
        btnJSBean.isSend = true
        sendBean = btnJSBean
        sendJDQControl()
        ivJS.setImageResource(R.mipmap.btn_humidification_selected)
        ivCS.setImageResource(R.mipmap.btn_dehumidification_normal)
        ivSDOFF.setImageResource(R.mipmap.btn_close_normal)
    }

    private fun CS() {
        Logger.i("正在打开除湿功能")
        btnJSBean.isOpen = false
        btnJSBean.isSend = true
        sendBean = btnJSBean
        sendJDQControl()
        btnCSBean.isSend = true
        btnCSBean.isOpen = true
        sendBean = btnCSBean
        sendJDQControl()
        ivJS.setImageResource(R.mipmap.btn_humidification_normal)
        ivCS.setImageResource(R.mipmap.btn_dehumidification_selected)
        ivSDOFF.setImageResource(R.mipmap.btn_close_normal)
    }

    private fun XFON() {
        Logger.i("正在打开新风功能")
        isXFTime = false
        btnXFBean.isOpen = true
        btnXFBean.isSend = true
        sendBean = btnXFBean
        sendJDQControl()
        ivXFOFF.setImageResource(R.mipmap.btn_close_normal)
        ivXFON.setImageResource(R.mipmap.btn_open_selected)
        ivTime.setImageResource(R.mipmap.btn_ontime_normal)
        SharedPreferencesUtil.getInstance(this).saveData(Constants.ISOPEN_XFTIME, false)
    }

    private fun XFTime() {
        Logger.i("正在打开新风定时功能")
        isXFTime = true
        SharedPreferencesUtil.getInstance(this).saveData(Constants.ISOPEN_XFTIME, true)
        controlXFTime(true)
        ivXFOFF.setImageResource(R.mipmap.btn_close_normal)
        ivXFON.setImageResource(R.mipmap.btn_open_normal)
        ivTime.setImageResource(R.mipmap.btn_ontime_selected)
    }

    /**
     * 冷暖全关方法
     */
    private fun LNAllOff() {
        isHeating = false
        isCryogen = false
        if (btnHeatBean.isOpen) {
            btnHeatBean.isOpen = false
            btnHeatBean.isSend = true
            sendBean = btnHeatBean
            sendJDQControl()
        }
        if (btnClodBean.isOpen) {
            btnClodBean.isOpen = false
            btnClodBean.isSend = true
            sendBean = btnClodBean
            sendJDQControl()
        }
        runOnUiThread {
            ivClod.setImageResource(R.mipmap.btn_cool_normal)
            ivHeat.setImageResource(R.mipmap.btn_hot_normal)
            ivLNOFF.setImageResource(R.mipmap.btn_close_selected)
        }
    }

    /**
     * 湿度全关方法
     */
    private fun SDAllOff() {
        if (btnJSBean.isOpen) {
            btnJSBean.isOpen = false
            btnJSBean.isSend = true
            sendBean = btnJSBean
            sendJDQControl()
        }
        if (btnCSBean.isOpen) {
            btnCSBean.isOpen = false
            btnCSBean.isSend = true
            sendBean = btnCSBean
            sendJDQControl()
        }
        runOnUiThread {
            ivJS.setImageResource(R.mipmap.btn_humidification_normal)
            ivCS.setImageResource(R.mipmap.btn_dehumidification_normal)
            ivSDOFF.setImageResource(R.mipmap.btn_close_selected)
        }

    }

    /**
     * 新风全关方法
     */
    private fun XFAllOff() {
        isXFTime = false
        btnXFBean.isOpen = false
        btnXFBean.isSend = true
        sendBean = btnXFBean
        sendJDQControl()
        runOnUiThread {
            ivXFOFF.setImageResource(R.mipmap.btn_close_selected)
            ivXFON.setImageResource(R.mipmap.btn_open_normal)
            ivTime.setImageResource(R.mipmap.btn_ontime_normal)
        }
        SharedPreferencesUtil.getInstance(this).saveData(Constants.ISOPEN_XFTIME, false)
    }

    private fun controlXFTime(isOpen: Boolean) {
        var calendar = Calendar.getInstance()
        var curTime = calendar.timeInMillis
        var tfTime = (SharedPreferencesUtil.getInstance(this).getData(
            Constants.DEFAULT_SETTING_TFTIME,
            Constants.DEFAULT_TF_TIME
        ) as String)
            .split("分钟")[0].toInt()
        if (isOpen) {//打开新风，计算下次关闭时间
            btnXFBean.isOpen = true
            btnXFBean.isSend = true
            sendBean = btnXFBean
            sendJDQControl()
            nextTime = curTime + tfTime * 1000 * 60
            Logger.i(
                "新风定时功能启动中，新风正在运行,运行时间为${tfTime}分钟，" +
                        "已计算出下次新风关闭时间为：${SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date(nextTime))}"
            )
        } else { //关闭新风，计算下次打开时间
            btnXFBean.isOpen = false
            btnXFBean.isSend = true
            sendBean = btnXFBean
            sendJDQControl()
            nextTime = curTime + (60 - tfTime) * 1000 * 60
            Logger.i(
                "新风定时功能启动中，新风已关闭，" +
                        "已计算出下次新风开启时间时间为：${SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date(nextTime))}"
            )

        }
    }

    /**
     * 规则：从又到左0/1/2
     *
     */
    private fun recordingBtnState(type: String, state: Int) {
        when (type) {
            "ln" -> {
                SharedPreferencesUtil.getInstance(this).saveData(Constants.BTN_STATE_LN, state)
            }
            "sd" -> {
                SharedPreferencesUtil.getInstance(this).saveData(Constants.BTN_STATE_SD, state)
            }
            "xf" -> {
                SharedPreferencesUtil.getInstance(this).saveData(Constants.BTN_STATE_XF, state)
            }
        }
    }

    private fun restoreBtnState() {
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_LN, 0)) {
            0 -> {//冷暖全关
                LNAllOff()
            }
            1 -> {//制热
                ZR()
            }
            2 -> {//制冷
                ZL()
            }
        }
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_SD, 0)) {
            0 -> {//湿度全关
                SDAllOff()
            }
            1 -> {//除湿
                CS()
            }
            2 -> {//加湿
                JS()
            }
        }
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_XF, 1)) {
            0 -> {//定时
                XFTime()
            }
            1 -> {//新风关闭
                XFAllOff()
            }
            2 -> {//开启新风
                XFON()
            }
        }
    }

    internal inner class EnvAirBoradCast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI) {
                val data = intent.getStringExtra("msg")
                buffer.append(data)

                if (buffer.toString().length > 4 && buffer.toString().substring(
                        0,
                        4
                    ) == "0103"
                ) {//环境探测器数据EnvQ3
                    val message = Message.obtain()
                    message.what = 0
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 4 && buffer.toString().substring(
                        0,
                        6
                    ) == "01032e"
                ) {//环境探测器数据Ate24V
                    val message = Message.obtain()
                    message.what = 3
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 4 &&
                    buffer.toString().substring(0, 4).toLowerCase() == "AA55".toLowerCase() &&
                    buffer.toString().substring(
                        buffer.length - 4,
                        buffer.length
                    ).toLowerCase() == "0D0A".toLowerCase()
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