package com.wusy.serialportproject.ui.test

import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.wusylibrary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bottom.*

class BottomActivity :BaseTouchActivity(){
    override fun getContentViewId(): Int {
        return R.layout.activity_bottom
    }

    override fun findView() {
    }

    override fun init() {
        top.setOnClickListener {
            startFinish()
        }
    }

    override fun onFingerBottomTouch() {
        super.onFingerBottomTouch()
        startFinish()
    }
    private fun startFinish(){
        finish()
        overridePendingTransition(R.anim.top_to_center, R.anim.center_to_bottom)
    }
}
