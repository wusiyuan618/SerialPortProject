package com.wusy.serialportproject.ui.test

import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.wusylibrary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_left.*

class LeftActivity : BaseTouchActivity(){
    override fun getContentViewId(): Int {
        return R.layout.activity_left
    }

    override fun findView() {
    }

    override fun init() {
        right.setOnClickListener {
            startFinish()
        }
    }

    override fun onFingerLeftTouch() {
        super.onFingerLeftTouch()
        startFinish()
    }
    private fun startFinish(){
        finish()
        overridePendingTransition(R.anim.right_to_center, R.anim.center_to_left)
    }

}
