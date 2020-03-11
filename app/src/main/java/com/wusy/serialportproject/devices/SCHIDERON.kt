package com.wusy.serialportproject.devices

class SCHIDERON:BaseDevices(){


    init {
        this.name="SCHIDERON"
        this.type="JDQ"
        this.tips="寄电器，未使用"

        /**
         * ------------------------------------------------
         * SCHIDERON寄电器命令
         * ------------------------------------------------
         */
        /**
         * 查询寄电器的灯亮状态
         * 返回实例：AA 55 00 04 05 06 64 00 3F 00 0D 0A
         * 3F位即灯亮的状态数据位
         */
        this.SearchStatusCode = "AA550400030664000D0A"
    }
    fun parseStatusData(msgObj:String):ArrayList<Int>{
        val data = msgObj.substring(16, 18)
        return getStatus(data, 8)
    }
    fun getSendControlCode(code:String): String {
        return "AA550400031E" + code + "FF0D0A"
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
