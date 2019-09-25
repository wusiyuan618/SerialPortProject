package com.wusy.serialportproject.ui

import android.view.View
import android.widget.RelativeLayout
import com.wusy.serialportproject.R
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.base.BaseFragment

class SystemSettingFragment:BaseFragment(){
    lateinit var rlReSet:RelativeLayout
    lateinit var rlSetPhone:RelativeLayout
    lateinit var rlUpdateLog:RelativeLayout

    override fun findView(view: View?) {
        rlReSet=view!!.findViewById(R.id.rlReSet)
        rlSetPhone=view.findViewById(R.id.rlSetPhone)
        rlUpdateLog=view.findViewById(R.id.rlUpdateLog)
    }




    override fun init() {
        rlReSet.setOnClickListener {

        }
        rlSetPhone.setOnClickListener {

        }
        rlUpdateLog.setOnClickListener {

        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_system
    }

}