package com.wusy.serialportproject.ui

import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.wusy.serialportproject.R
import com.wusy.serialportproject.adapter.SettingAdapter
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.bean.SettingBean
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.base.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity: BaseTouchActivity(){
    lateinit var adapter:SettingAdapter
    override fun findView() {

    }

    override fun init() {
        ivBack.setOnClickListener {
            finish()
        }
        recyclerView.layoutManager=LinearLayoutManager(this)
        adapter=SettingAdapter(this)
        adapter.list=createList()
        adapter.fm=supportFragmentManager
        recyclerView.adapter=adapter
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_setting
    }
    private fun createList():ArrayList<SettingBean>{
        var list=ArrayList<SettingBean>()
        val ft=supportFragmentManager.beginTransaction()
        list.add(SettingBean().apply {
            this.title="屏保时间"
            this.isSelect=true
            this.fragment=ScreenSettingFragment()
            ft.add(R.id.fragmentBox,this.fragment as ScreenSettingFragment,"ScreenSettingFragment")
        })
        list.add(SettingBean().apply {
            this.title="环境设置"
            this.isSelect=false
            this.fragment=FreshSettingFragment()
            ft.add(R.id.fragmentBox,this.fragment as FreshSettingFragment,"FreshSettingFragment")
        })
        list.add(SettingBean().apply {
            this.title="增值服务"
            this.isSelect=false
            this.fragment=ValueAddedFragment()
            ft.add(R.id.fragmentBox,this.fragment as ValueAddedFragment,"ValueAddedFragment")
        })
        list.add(SettingBean().apply {
            this.title="系统设置"
            this.isSelect=false
            this.fragment=SystemSettingFragment()
            this.needPwd=true
            ft.add(R.id.fragmentBox,this.fragment as SystemSettingFragment,"SystemSettingFragment")
        })
        ft.commit()
        return list
    }

}
