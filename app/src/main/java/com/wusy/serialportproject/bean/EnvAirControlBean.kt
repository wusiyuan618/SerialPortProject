package com.wusy.serialportproject.bean

class EnvAirControlBean{
    companion object {
        /**
         * 制冷
         */
        const val TYPE_Cryogen = 0
        /**
         * 制热
         */
        const val TYPE_Heating = 1
        /**
         * 新风
         */
        const val TYPE_Hairdryer = 2
        /**
         * 除湿
         */
        const val TYPE_Dehumidification = 3
        /**
         * 加湿
         */
        const val TYPE_Humidification = 4

    }
    var isSend=false
    var switchIndex=0
    var isOpen=false
    var type:Int?=null
    var content:String?=null
    var imgResourceSelect:Int?=null
    var imgResourceNormal:Int?=null
    override fun toString(): String {
        return "EnvAirControlBean(isSend=$isSend, switchIndex=$switchIndex, isOpen=$isOpen, type=$type, content=$content, imgResourceSelect=$imgResourceSelect, imgResourceNormal=$imgResourceNormal)"
    }


}