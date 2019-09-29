package com.wusy.serialportproject.ui.test

import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.wusylibrary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity:BaseTouchActivity(){
    override fun getContentViewId(): Int {
        return R.layout.activity_top
    }

    override fun findView() {
    }

    override fun init() {
       bottom.setOnClickListener {
           startFinish()
       }
    }

    override fun onFingerTopTouch() {
        super.onFingerTopTouch()
        startFinish()
    }
    private fun startFinish(){
        finish()
        overridePendingTransition(R.anim.bottom_to_center, R.anim.center_to_top)
    }
}
