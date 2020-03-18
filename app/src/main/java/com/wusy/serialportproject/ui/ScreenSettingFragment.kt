package com.wusy.serialportproject.ui

import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil

class ScreenSettingFragment:BaseFragment(){
    lateinit var tvTitle:TextView
    lateinit var tvContent:TextView
    lateinit var wheel:WheelPicker
    lateinit var tvKQZL:TextView
    lateinit var tvMNSZ:TextView
    lateinit var tvSZSZ:TextView
    lateinit var tvXP:TextView

    override fun init() {
        tvTitle.text="屏保时间"
        tvContent.text="请设置锁屏时间"
        wheel.data= createList()
        wheel.setSelectedItemPosition(SharedPreferencesUtil.getInstance(context).getData(Constants.SCREEN_SETTING_POSITION,5).toString().toInt(),false)
        wheel.setOnItemSelectedListener { _, data, position ->
            Logger.i("屏保时间修改为：${data}")
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_TIME,data)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_POSITION,position)
        }
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//禁止息屏

        when(SharedPreferencesUtil.getInstance(context).getData(Constants.SCREEN_SETTING_POSITION,0)){
            0->{
                hideAll()
                tvKQZL.alpha=1f
            }
            1->{
                hideAll()
                tvMNSZ.alpha=1f
            }
            2->{
                hideAll()
                tvSZSZ.alpha=1f
            }
            3->{
                hideAll()
                tvXP.alpha=1f
            }
        }
        tvKQZL.setOnClickListener {
            hideAll()
            tvKQZL.alpha=1f
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_MODEL_TYPE,0)
        }
        tvMNSZ.setOnClickListener {
            hideAll()
            tvMNSZ.alpha=1f
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_MODEL_TYPE,1)

        }
        tvSZSZ.setOnClickListener {
            hideAll()
            tvSZSZ.alpha=1f
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_MODEL_TYPE,2)

        }
        tvXP.setOnClickListener {
            hideAll()
            tvXP.alpha=1f
            SharedPreferencesUtil.getInstance(context).saveData(Constants.SCREEN_SETTING_MODEL_TYPE,3)

        }

    }
    private fun hideAll(){
        tvKQZL.alpha=0.2f
        tvMNSZ.alpha=0.2f
        tvSZSZ.alpha=0.2f
        tvXP.alpha=0.2f
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_screen
    }

    override fun findView(view: View?) {
        tvTitle=view!!.findViewById(R.id.tvTitle)
        tvContent=view.findViewById(R.id.tvContent)
        wheel=view.findViewById(R.id.wheel)
        tvKQZL=view.findViewById(R.id.tvKQZL)
        tvMNSZ=view.findViewById(R.id.tvMNSZ)
        tvSZSZ=view.findViewById(R.id.tvSZSZ)
        tvXP=view.findViewById(R.id.tvXP)

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
