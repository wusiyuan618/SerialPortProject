package com.wusy.serialportproject.ui.screen

import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import kotlinx.android.synthetic.main.activity_screen_clock.*

class ScreenClockActivity: BaseTouchActivity(){
    override fun findView() {
    }

    override fun init() {
        Logger.i("Clock屏保启动")
        Constants.isOpenScreen=true
        layout_total.setOnClickListener {
            finish()
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_screen_clock
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i("Clock屏保消失")
        Constants.isOpenScreen=false
    }
}
