package com.wusy.serialportproject.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wusy.serialportproject.R
import com.wusy.serialportproject.bean.EnvAirControlBean
import com.wusy.serialportproject.viewHolder.EnvAriControlViewHolder
import com.wusy.wusylibrary.base.BaseRecyclerAdapter

class EnvAirAdapter(context: Context) : BaseRecyclerAdapter<EnvAirControlBean>(context){
    override fun onMyCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return EnvAriControlViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_air_control, parent, false)
        )
    }

    override fun onMyBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if(holder is EnvAriControlViewHolder){
            val thisHolder=holder as EnvAriControlViewHolder
            thisHolder.ivBg.setBackgroundResource(if(list[position].isOpen ) list[position].imgResourceSelect?:0 else list[position].imgResourceNormal?:0 )
            thisHolder.tvContent.text=list[position].content
        }
    }

}
