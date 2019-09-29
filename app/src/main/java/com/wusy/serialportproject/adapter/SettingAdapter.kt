package com.wusy.serialportproject.adapter

import android.content.Context
import android.graphics.Color
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.SettingBean
import com.wusy.serialportproject.popup.NumberBoxPopup
import com.wusy.serialportproject.viewHolder.SettingViewHolder
import com.wusy.wusylibrary.base.BaseRecyclerAdapter

class SettingAdapter(context: Context) : BaseRecyclerAdapter<SettingBean>(context) {
    var fm: FragmentManager? = null
    val numberBoxPopup = NumberBoxPopup(context)
    var pwds = ArrayList<String>()
    override fun onMyCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return SettingViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_setting_list, parent, false)
        )
    }

    override fun onMyBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is SettingViewHolder) {
            val thisHolder = holder as SettingViewHolder
            thisHolder.tv.text = list[position].title
            if (list[position].isSelect == true) {
                thisHolder.tv.setBackgroundResource(R.mipmap.btn_setting_bg)
                thisHolder.tv.setTextColor(Color.parseColor("#ffffff"))
                change(position)
            } else {
                thisHolder.tv.background = null
                thisHolder.tv.setTextColor(Color.parseColor("#666666"))
            }
            thisHolder.tv.setOnClickListener {
                if (list[position].needPwd) {
                    beginEditPwd(position)
                } else {
                    changeSelectStatus(position)
                }
            }
        }
    }

    private fun changeSelectStatus(position: Int) {
        for (bean in list) {
            bean.isSelect = false
        }
        list[position].isSelect = true
        notifyDataSetChanged()
    }

    private fun change(position: Int) {
        fm?.let {
            val ft = it.beginTransaction()
            for (bean in list) {
                ft.hide(bean.fragment!!)
            }
            ft.show(list[position].fragment!!)
            ft.commit()
        }
    }

    private fun beginEditPwd(position: Int) {
        pwds.clear()
        numberBoxPopup.pwdIndicator.clear()
        numberBoxPopup.showPopupWindow()
        numberBoxPopup.pwdIndicator.initIndicator(6)
        numberBoxPopup.numberKeyBoxView.setNumberKeyBoxViewClick {
            when (it) {
                "ok" -> {
                    if (pwds.size < 6) {
                        Toast.makeText(context, "请正确输入6位数密码", Toast.LENGTH_SHORT).show()
                        return@setNumberKeyBoxViewClick
                    }
                    var pwd: String = ""
                    for (i in pwds.indices) {
                        pwd += pwds[i]
                    }
                    if (pwd == Constants.DEFAULT_SYSTEM_SETTINGPWD) {
                        changeSelectStatus(position)
                    } else {
                        Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show()
                    }
                    numberBoxPopup.dismiss()
                }
                "delete" -> {
                    if (numberBoxPopup.pwdIndicator.remove()) {
                        if (pwds.size != 0) pwds.removeAt(pwds.size - 1)
                    }
                }
                else -> {
                    if (numberBoxPopup.pwdIndicator.add()) {
                        pwds.add(it)
                    }
                }
            }
        }
    }
}