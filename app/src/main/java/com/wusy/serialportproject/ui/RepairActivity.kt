package com.wusy.serialportproject.ui

import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.util.DataUtils
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_repair.*

class RepairActivity :BaseTouchActivity(){

    override fun findView() {

    }

    override fun init() {
        tvDLSPhone.text="代理商联系电话:"+ DataUtils.formatPhoneNumber(SharedPreferencesUtil.getInstance(this).getData(
            Constants.DEFAULT_DLS_PHONE, Constants.DEFAULT_DLS_PHONENUMBER).toString()," - ")
        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_repair
    }

}
