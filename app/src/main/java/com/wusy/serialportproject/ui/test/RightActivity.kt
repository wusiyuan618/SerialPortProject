package com.wusy.serialportproject.ui.test

import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.wusylibrary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_right.*

class RightActivity : BaseTouchActivity(){
    override fun getContentViewId(): Int {
        return R.layout.activity_right
    }

    override fun findView() {
    }

    override fun init() {
        left.setOnClickListener {
            startFinish()
        }
    }

    override fun onFingerRightTouch() {
        super.onFingerRightTouch()
        startFinish()
    }
    private fun startFinish(){
        finish()
        overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right)
    }

}
