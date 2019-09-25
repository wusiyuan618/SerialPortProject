package com.wusy.serialportproject.ui

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil

class FreshSettingFragment:BaseFragment(){
    lateinit var tvTitle:TextView
    lateinit var tvContent:TextView
    lateinit var wheel:WheelPicker
    override fun init() {
        tvTitle.text="新风时间"
        tvContent.text="设置开启新风持续时间"
        wheel.data= createList()
        wheel.setSelectedItemPosition(SharedPreferencesUtil.getInstance(context).getData(Constants.FRESHSETTINGPOSITION,0).toString().toInt(),false)
        wheel.setOnItemSelectedListener { _, data, position ->
            SharedPreferencesUtil.getInstance(context).saveData(Constants.FRESHSETTINGTIME,data)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.FRESHSETTINGPOSITION,position)
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
