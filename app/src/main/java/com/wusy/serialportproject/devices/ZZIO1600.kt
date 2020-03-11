package com.wusy.serialportproject.devices

import com.wusy.serialportproject.util.DataUtils


class ZZIO1600:BaseDevices(){


    init {
        this.name= "ZZIO1600"
        this.type="JDQ"
        this.tips="寄电器，16口"

        /**
         * 默认查询16个继电器的状态
         */
        this.SearchStatusCode= "FE0100000010" + DataUtils.getCRC("FE0100000010")
    }
    fun getSendControlCode( index:Int, isOpen:Boolean): String {
        val star = "FE"
        val address = "05"
        var indexStr = ""
        if (index < 16)
            indexStr = "000" + Integer.toHexString(index)
        else
            indexStr = "0015"//最多16个
        var openStr = ""
        if (isOpen)
            openStr = "FF00"
        else
            openStr = "0000"
        return star + address + indexStr + openStr + DataUtils.getCRC(star + address + indexStr + openStr)
    }
    fun parseStatusData(msgObj:String):ArrayList<Int>{
        val data = msgObj.substring(6, 10)
        var list = getStatus(data, 16)
        //解析到的list含义前8位，代表的继电器9--16，后8位代表的继电器1--8
        //例如[0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0]代表1/2/5/12继电器点亮
        //这里我们从新组装数据的顺序，让1--16对应
        var listNew = ArrayList<Int>()
        for (i in 8 until 16) {
            listNew.add(list[i])
        }
        for (i in 0 until 8) {
            listNew.add(list[i])
        }
        return listNew
    }
    /**
     * 获取寄电器开关的状态数据
     */
    private fun getStatus(data: String, len: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        for (i in 0 until len) {
            list.add(Integer.parseInt(data, 16) shr i and 1)
        }
        return list
    }
}
