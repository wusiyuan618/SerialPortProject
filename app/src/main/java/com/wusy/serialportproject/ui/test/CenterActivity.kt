package com.wusy.serialportproject.ui.test

import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity

class CenterActivity : BaseTouchActivity() {
    override fun getContentViewId(): Int {
        return R.layout.activity_center
    }

    override fun findView() {

    }

    override fun init() {

    }



    override fun onFingerLeftTouch() {
        super.onFingerLeftTouch()
        startRightActivity()
    }

    override fun onFingerRightTouch() {
        super.onFingerRightTouch()
        startLeftActivity()
    }

    override fun onFingerBottomTouch() {
        super.onFingerBottomTouch()
        startTopActivity()
    }

    override fun onFingerTopTouch() {
        super.onFingerTopTouch()
        startBottomActivity()
    }

    private fun startRightActivity() {
        navigateTo(RightActivity::class.java)
        overridePendingTransition(R.anim.right_to_center, R.anim.center_to_left)
    }

    private fun startLeftActivity() {
        navigateTo(LeftActivity::class.java)
        overridePendingTransition(R.anim.left_to_center, R.anim.center_to_right)
    }

    private fun startBottomActivity() {
        navigateTo(BottomActivity::class.java)
        overridePendingTransition(R.anim.bottom_to_center, R.anim.center_to_top)
    }

    private fun startTopActivity() {
        navigateTo(TopActivity::class.java)
        overridePendingTransition(R.anim.top_to_center, R.anim.center_to_bottom)
    }
}
