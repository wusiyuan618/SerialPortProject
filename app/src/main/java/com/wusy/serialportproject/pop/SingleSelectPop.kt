package com.wusy.serialportproject.pop



import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.Animation
import com.aigestudio.wheelpicker.WheelPicker
import com.wusy.serialportproject.R
import razerdp.basepopup.BasePopupWindow
import java.util.ArrayList

class SingleSelectPop(context: Context) : BasePopupWindow(context) {
    var wheel = findViewById<WheelPicker>(R.id.thisWheel)
    var list: ArrayList<String>? = null
    init {
        setBlurBackgroundEnable(true)
    }

    override fun onCreateShowAnimation(): Animation {
        return getDefaultAlphaAnimation(true)
    }

    override fun onCreateDismissAnimation(): Animation {
        return getDefaultAlphaAnimation(false)
    }

    override fun onCreateContentView(): View {
        return createPopupById(R.layout.dialog_selectbuild)
    }

    fun init(strList: ArrayList<String>, default: String,listener: OnSingleSelectPopSelectListener) {
        list = strList
        wheel.data = strList
        for (index in strList.indices) {
            if (default == strList[index]) {
                wheel.setSelectedItemPosition(index, false)
                break
            }
        }
        wheel.setOnItemSelectedListener { picker, data, position ->
            listener.onSelect(data,position)
        }
    }

    interface OnSingleSelectPopSelectListener {
        fun onSelect(data: Any?, postion: Int)
    }
}
