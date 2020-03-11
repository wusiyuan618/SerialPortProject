package com.wusy.serialportproject.devices

import java.io.Serializable

open abstract class BaseDevices:Serializable{
    /**
     * 设备名字
     */
    var name:String=""
    /**
     * 设备类型
     */
    var type:String=""
    /**
     * 状态查询码
     */
    var SearchStatusCode:String=""
    /**
     * 备注
     */
    var tips:String=""
}
