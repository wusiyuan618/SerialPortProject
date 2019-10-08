package com.wusy.serialportproject.ui

import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.popup.MakeSurePopup
import com.wusy.serialportproject.popup.NumberEditPopup
import com.wusy.serialportproject.util.DataUtils
import com.wusy.serialportproject.util.InterAddressUtil
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.OkHttpUtil
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class SystemSettingFragment : BaseFragment() {
    lateinit var rlReSet: RelativeLayout
    lateinit var rlSetPhone: RelativeLayout
    lateinit var rlUpdateLog: RelativeLayout

    lateinit var reSetMakeSurePopup: MakeSurePopup
    lateinit var updateLogMakeSurePopup: MakeSurePopup
    lateinit var setPhoneNumberEidtPopup: NumberEditPopup

    override fun findView(view: View?) {
        rlReSet = view!!.findViewById(R.id.rlReSet)
        rlSetPhone = view.findViewById(R.id.rlSetPhone)
        rlUpdateLog = view.findViewById(R.id.rlUpdateLog)
    }


    override fun init() {
        initReSetPop()
        initUpdateLogPop()
        initSetPhonePop()
        rlReSet.setOnClickListener {
            reSetMakeSurePopup.showPopupWindow()
        }
        rlSetPhone.setOnClickListener {
            setPhoneNumberEidtPopup.tvEditContent.text=""
            setPhoneNumberEidtPopup.showPopupWindow()
        }
        rlUpdateLog.setOnClickListener {
            updateLogMakeSurePopup.showPopupWindow()
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_system
    }

    private fun initReSetPop() {
        reSetMakeSurePopup = MakeSurePopup(context)
        reSetMakeSurePopup.tvTitle.text = "恢复出厂设置"
        reSetMakeSurePopup.tvContent.text = "您是否需要恢复出厂设置呢？"
        reSetMakeSurePopup.ivSure.setOnClickListener {
            showLoadImage()
            Thread(Runnable {
                Thread.sleep(2000)
                activity!!.runOnUiThread {
                    hideLoadImage()
                    showToast("成功恢复出厂设置")
                    reSetMakeSurePopup.dismiss()
                }
            }).start()
        }
    }

    private fun initUpdateLogPop() {
        updateLogMakeSurePopup = MakeSurePopup(context)
        updateLogMakeSurePopup = MakeSurePopup(context)
        updateLogMakeSurePopup.tvTitle.text = "发送运行分析数据"
        updateLogMakeSurePopup.tvContent.text = "您是否需要发送运行分析数据呢？"
        updateLogMakeSurePopup.ivSure.setOnClickListener {
            updateLogMakeSurePopup.dismiss()
            showLoadImage()
            updateLog()
        }
    }

    /**
     * 上传日志
     *  最新日志下载地址---http://www.hjlapp.com:9201/root/pic/backGroundImg/logs/LogsByWusyLib_0.log
     *
     */
    private fun updateLog(){
        Logger.i("-----------------设备信息------------------")
        Logger.i("设备制造商：" + Build.MANUFACTURER)
        Logger.i("设备品牌：" + Build.BRAND)
        Logger.i("设备型号：" + Build.MODEL)
        Logger.i("系统版本：" + Build.VERSION.RELEASE)
        Logger.i("mac地址："+ InterAddressUtil.getMacAddress())
        Logger.i("-------------------------------------------")
        var url = "http://www.hjlapp.com:9202/fileUpload/uploadFile"
        var file = File(Environment.getExternalStorageDirectory().toString() + "/logger/LogsByWusyLib_0.log")
        var maps = HashMap<String, String>()
        maps["type"] = "1"
        OkHttpUtil.getInstance().upLoadFile(url, "file", file, maps, object : OkHttpUtil.ResultCallBack {
            override fun failListener(call: Call?, e: IOException?, message: String?) {
                activity!!.runOnUiThread {
                    showToast(message)
                    hideLoadImage()
                }
            }

            override fun successListener(call: Call?, response: Response?) {
                var json = JSONObject(response?.body()?.string())
                activity!!.runOnUiThread {
                    if (json.getString("status") == "0")
                        showToast("上传成功")
                    else
                        showToast("上传失败")
                    hideLoadImage()
                }
            }
        })
    }

    private fun initSetPhonePop() {
        setPhoneNumberEidtPopup = NumberEditPopup(context)
        setPhoneNumberEidtPopup.tvTitle.text = "发送运行分析数据"
        setPhoneNumberEidtPopup.tvContent.text = "当前代理商电话：" + DataUtils.formatPhoneNumber(SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_DLS_PHONE, Constants.DEFAULT_DLS_PHONENUMBER).toString()," - ")
        setPhoneNumberEidtPopup.numberKeyBoxView.setNumberKeyBoxViewClick {
            when (it) {
                "ok" -> {
                    if (setPhoneNumberEidtPopup.tvEditContent.text.toString().length != 11) {
                        Toast.makeText(context, "请正确输入11位的手机号码", Toast.LENGTH_SHORT).show()
                        return@setNumberKeyBoxViewClick
                    }
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULT_DLS_PHONE, setPhoneNumberEidtPopup.tvEditContent.text.toString())
                    setPhoneNumberEidtPopup.tvEditContent.text = ""
                    setPhoneNumberEidtPopup.tvContent.text = "当前代理商电话：" + DataUtils.formatPhoneNumber(SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_DLS_PHONE, Constants.DEFAULT_DLS_PHONENUMBER).toString()," - ")
                    setPhoneNumberEidtPopup.dismiss()
                    showToast("修改成功")
                }
                "delete" -> {
                    if (setPhoneNumberEidtPopup.tvEditContent.text.toString().isNotEmpty()) {
                        setPhoneNumberEidtPopup.tvEditContent.text =
                            setPhoneNumberEidtPopup.tvEditContent.text.toString()
                                .substring(0, setPhoneNumberEidtPopup.tvEditContent.text.toString().length - 1)
                    }
                }
                else -> {
                    setPhoneNumberEidtPopup.tvEditContent.text =
                        setPhoneNumberEidtPopup.tvEditContent.text.toString() + it
                }
            }
        }
    }
}