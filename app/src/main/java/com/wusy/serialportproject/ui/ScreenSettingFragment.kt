package com.wusy.serialportproject.ui

import android.view.View
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil

class ScreenSettingFragment:BaseFragment(){
    lateinit var tvTitle:TextView
    lateinit var tvContent:TextView
    lateinit var wheel:WheelPicker
    override fun init() {
        tvTitle.text="屏保时间"
        tvContent.text="请设置锁屏时间"
        wheel.data= createList()
        wheel.setSelectedItemPosition(SharedPreferencesUtil.getInstance(context).getData(Constants.SCREEN_SETTING_POSITION,5).toString().toInt(),false)
        wheel.setOnItemSelectedListener { _, data, position ->
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_TIME,data)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_POSITION,position)
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_screen
    }

    override fun findView(view: View?) {
        tvTitle=view!!.findViewById(R.id.tvTitle)
        tvContent=view.findViewById(R.id.tvContent)
        wheel=view.findViewById(R.id.wheel)
    }

    private fun createList():ArrayList<String>{
        var list=ArrayList<String>()
        list.add("1分钟")
        list.add("3分钟")
        list.add("5分钟")
        list.add("10分钟")
        list.add("15分钟")
        list.add("30分钟")
        list.add("60分钟")
        list.add("从不")
        return list
    }
}
