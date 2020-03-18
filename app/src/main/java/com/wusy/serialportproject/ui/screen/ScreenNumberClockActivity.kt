package com.wusy.serialportproject.ui.screen

import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import kotlinx.android.synthetic.main.activity_screen_numberclock.*
import java.text.SimpleDateFormat
import java.util.*

class ScreenNumberClockActivity: BaseTouchActivity(){
    override fun findView() {
    }

    override fun init() {
        Logger.i("Clock屏保启动")
        Constants.isOpenScreen=true
        layout_total.setOnClickListener {
            finish()
        }
        Thread(Runnable {
            while (true) {
                runOnUiThread {
                    val calendar = Calendar.getInstance()
                    tvTime.text = SimpleDateFormat("hh:mm").format(calendar.time)
                    tvContent.text = (if (Calendar.AM == calendar.get(Calendar.AM_PM)) "上午" else "下午")+ " - 星期" + calendar.get(
                        Calendar.DAY_OF_WEEK)+" - "+ (calendar.get(Calendar.MONTH) + 1).toString() + "月" +
                            calendar.get(Calendar.DAY_OF_MONTH).toString() + "日"
                }
                Thread.sleep(1000)
            }
        }).start()
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_screen_numberclock
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i("Clock屏保消失")
        Constants.isOpenScreen=false
    }
}

