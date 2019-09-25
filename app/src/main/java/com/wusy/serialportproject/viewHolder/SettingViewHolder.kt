package com.wusy.serialportproject.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.wusy.serialportproject.R

class SettingViewHolder(itemView: View)  :RecyclerView.ViewHolder(itemView){
    val tv=itemView.findViewById<TextView>(R.id.tv)
}