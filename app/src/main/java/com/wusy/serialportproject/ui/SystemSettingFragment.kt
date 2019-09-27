package com.wusy.serialportproject.ui

import android.content.SharedPreferences
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.popup.MakeSurePopup
import com.wusy.serialportproject.popup.NumberEditPopup
import com.wusy.serialportproject.util.DataUtils
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil

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
            showLoadImage()
            Thread(Runnable {
                Thread.sleep(2000)
                activity!!.runOnUiThread {
                    hideLoadImage()
                    showToast("成功上传日志")
                    updateLogMakeSurePopup.dismiss()
                }
            }).start()
        }
    }

    private fun initSetPhonePop() {
        setPhoneNumberEidtPopup = NumberEditPopup(context)
        setPhoneNumberEidtPopup.tvTitle.text = "发送运行分析数据"
        setPhoneNumberEidtPopup.tvContent.text = "当前代理商电话：" + DataUtils.formatPhoneNumber(SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULTDLSPHONE, Constants.DEFAULTDLSPHONENUMBER).toString()," - ")
        setPhoneNumberEidtPopup.numberKeyBoxView.setNumberKeyBoxViewClick {
            when (it) {
                "ok" -> {
                    if (setPhoneNumberEidtPopup.tvEditContent.text.toString().length != 11) {
                        Toast.makeText(context, "请正确输入11位的手机号码", Toast.LENGTH_SHORT).show()
                        return@setNumberKeyBoxViewClick
                    }
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULTDLSPHONE, setPhoneNumberEidtPopup.tvEditContent.text.toString())
                    setPhoneNumberEidtPopup.tvEditContent.text = ""
                    setPhoneNumberEidtPopup.dismiss()
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