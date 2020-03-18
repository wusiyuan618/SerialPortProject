package com.wusy.serialportproject.ui

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.pop.SingleSelectPop
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import org.w3c.dom.Text

class FreshSettingFragment:BaseFragment(){
    lateinit var ivJNWD:ImageView
    lateinit var rlJNWD:RelativeLayout
    lateinit var llJNWDContent:LinearLayout
    lateinit var rlZLJNWD:RelativeLayout
    lateinit var tvZLJNWD:TextView
    lateinit var rlZRJNWD:RelativeLayout
    lateinit var tvZRJNWD:TextView
    var isJNWDOpen=false
    lateinit var temSingleSelectPopForJNZL:SingleSelectPop
    lateinit var temSingleSelectPopForJNZR:SingleSelectPop

    lateinit var ivLJWD:ImageView
    lateinit var rlLJWD:RelativeLayout
    lateinit var llLJWDContent:LinearLayout
    lateinit var rlZLLJWD:RelativeLayout
    lateinit var tvZLLJWD:TextView
    lateinit var rlZRLJWD:RelativeLayout
    lateinit var tvZRLJWD:TextView
    var isLJWDOpen=false
    lateinit var temSingleSelectPopForLJZL:SingleSelectPop
    lateinit var temSingleSelectPopForLJZR:SingleSelectPop

    lateinit var tvTFSJ:TextView
    lateinit var rlTFSJ:RelativeLayout
    lateinit var ivAdd:ImageView




    lateinit var temSingleSelectPopForTFSJ:SingleSelectPop

    override fun init() {
        /**
         * 节能温度(制冷)
         */
        temSingleSelectPopForJNZL= SingleSelectPop(activity!!)
        temSingleSelectPopForJNZL.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_JN_ZL,Constants.DEFAULT_TEMPDATA_JNZL) as String
            ,object:SingleSelectPop.OnSingleSelectPopSelectListener{
            override fun onSelect(data: Any?, postion: Int) {
                tvZLJNWD.text=data.toString()
                SharedPreferencesUtil.getInstance(context).saveData(Constants.DEFAULT_TEMP_JN_ZL,data)
                temSingleSelectPopForJNZL.dismiss()
            }

        })
        tvZLJNWD.text=SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_JN_ZL,Constants.DEFAULT_TEMPDATA_JNZL) as String
        rlZLJNWD.setOnClickListener {
            temSingleSelectPopForJNZL.showPopupWindow(tvZLJNWD)
        }
        /**
         * 节能温度(制热)
         */
        temSingleSelectPopForJNZR= SingleSelectPop(activity!!)
        temSingleSelectPopForJNZR.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_JN_ZR,Constants.DEFAULT_TEMPDATA_JNZR) as String
            ,object:SingleSelectPop.OnSingleSelectPopSelectListener{
                override fun onSelect(data: Any?, postion: Int) {
                    tvZRJNWD.text=data.toString()
                    SharedPreferencesUtil.getInstance(context).saveData(Constants.DEFAULT_TEMP_JN_ZR,data)
                    temSingleSelectPopForJNZR.dismiss()
                }

            })
        tvZRJNWD.text=SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_JN_ZR,Constants.DEFAULT_TEMPDATA_JNZR) as String
        rlZRJNWD.setOnClickListener {
            temSingleSelectPopForJNZR.showPopupWindow(tvZLJNWD)
        }
        rlJNWD.setOnClickListener {
            if(isJNWDOpen){
                llJNWDContent.visibility=View.GONE
                ivJNWD.setImageResource(R.mipmap.icon_xiaojiantou)
            }else{
                llJNWDContent.visibility=View.VISIBLE
                ivJNWD.setImageResource(R.mipmap.icon_xiaojiantoulv)
            }
            isJNWDOpen=!isJNWDOpen
        }
        /**
         * 离家温度(制冷)
         */
        temSingleSelectPopForLJZL= SingleSelectPop(activity!!)
        temSingleSelectPopForLJZL.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_OUTHOME_ZL,Constants.DEFAULT_TEMPDATA_LJZL) as String
            ,object:SingleSelectPop.OnSingleSelectPopSelectListener{
                override fun onSelect(data: Any?, postion: Int) {
                    tvZLLJWD.text=data.toString()
                    SharedPreferencesUtil.getInstance(context).saveData(Constants.DEFAULT_TEMP_OUTHOME_ZL,data)
                    temSingleSelectPopForLJZL.dismiss()
                }

            })
        tvZLLJWD.text= SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_OUTHOME_ZL,Constants.DEFAULT_TEMPDATA_LJZL) as String
        rlZLLJWD.setOnClickListener {
            temSingleSelectPopForLJZL.showPopupWindow(tvZLLJWD)
        }
        /**
         * 离家温度(制热)
         */
        temSingleSelectPopForLJZR= SingleSelectPop(activity!!)
        temSingleSelectPopForLJZR.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_OUTHOME_ZR,Constants.DEFAULT_TEMPDATA_LJZR) as String
            ,object:SingleSelectPop.OnSingleSelectPopSelectListener{
                override fun onSelect(data: Any?, postion: Int) {
                    tvZRLJWD.text=data.toString()
                    SharedPreferencesUtil.getInstance(context).saveData(Constants.DEFAULT_TEMP_OUTHOME_ZR,data)
                    temSingleSelectPopForLJZR.dismiss()
                }

            })
        tvZRLJWD.text= SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_TEMP_OUTHOME_ZR,Constants.DEFAULT_TEMPDATA_LJZR) as String
        rlZRLJWD.setOnClickListener {
            temSingleSelectPopForLJZR.showPopupWindow(tvZRLJWD)
        }
        rlLJWD.setOnClickListener {
            if(isLJWDOpen){
                llLJWDContent.visibility=View.GONE
                ivLJWD.setImageResource(R.mipmap.icon_xiaojiantou)
            }else{
                llLJWDContent.visibility=View.VISIBLE
                ivLJWD.setImageResource(R.mipmap.icon_xiaojiantoulv)
            }
            isLJWDOpen=!isLJWDOpen
        }
        /**
         * 通风时间
         */
        temSingleSelectPopForTFSJ= SingleSelectPop(activity!!)
        temSingleSelectPopForTFSJ.init(initTime(),
            SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_SETTING_TFTIME,Constants.DEFAULT_TF_TIME) as String
            ,object:SingleSelectPop.OnSingleSelectPopSelectListener{
                override fun onSelect(data: Any?, postion: Int) {
                    tvTFSJ.text=data.toString()
                    SharedPreferencesUtil.getInstance(context).saveData(Constants.DEFAULT_SETTING_TFTIME,data)
                    temSingleSelectPopForTFSJ.dismiss()
                }

            })
        tvTFSJ.text=SharedPreferencesUtil.getInstance(context).getData(Constants.DEFAULT_SETTING_TFTIME,Constants.DEFAULT_TF_TIME) as String
        rlTFSJ.setOnClickListener {
            temSingleSelectPopForTFSJ.showPopupWindow(tvTFSJ)
        }
        ivAdd.setOnClickListener {

        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_enr
    }

    override fun findView(view: View?) {
        ivJNWD= view?.findViewById(R.id.ivJNWD)!!
        rlJNWD= view.findViewById(R.id.rlJNWD)!!
        llJNWDContent= view.findViewById(R.id.llJNWDContent)!!
        rlZLJNWD= view.findViewById(R.id.rlZLJNWD)!!
        tvZLJNWD= view.findViewById(R.id.tvZLJNWD)!!
        rlZRJNWD= view.findViewById(R.id.rlZRJNWD)!!
        tvZRJNWD= view.findViewById(R.id.tvZRJNWD)!!

        ivLJWD= view.findViewById(R.id.ivLJWD)!!
        rlLJWD= view.findViewById(R.id.rlLJWD)!!
        llLJWDContent= view.findViewById(R.id.llLJWDContent)!!
        rlZLLJWD= view.findViewById(R.id.rlZLLJWD)!!
        tvZLLJWD= view.findViewById(R.id.tvZLLJWD)!!
        rlZRLJWD= view.findViewById(R.id.rlZRLJWD)!!
        tvZRLJWD= view.findViewById(R.id.tvZRLJWD)!!

        tvTFSJ= view.findViewById(R.id.tvTFSJ)!!
        rlTFSJ= view.findViewById(R.id.rlTFSJ)!!
        ivAdd= view.findViewById(R.id.ivAdd)!!

    }
    fun initTemp():ArrayList<String>{
        var list=ArrayList<String>()
        for(i in 16..30){
            list.add("${i}℃")
        }
        return list
    }
    fun initTime():ArrayList<String>{
        var list=ArrayList<String>()
        for(i in 1..59){
            list.add("${i}分钟")
        }
        return list
    }

}
