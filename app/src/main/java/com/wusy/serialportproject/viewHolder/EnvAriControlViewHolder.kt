package com.wusy.serialportproject.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wusy.serialportproject.R

class EnvAriControlViewHolder(itemView: View)  :RecyclerView.ViewHolder(itemView){
    val ivBg=itemView.findViewById<ImageView>(R.id.ivBg)
    val tvContent=itemView.findViewById<TextView>(R.id.tvContent)
}
