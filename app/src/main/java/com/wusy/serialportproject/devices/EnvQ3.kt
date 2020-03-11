package com.wusy.serialportproject.devices
class EnvQ3:BaseDevices(){
    init {
        this.name= "EnvQ3"
        this.type="Env"
        this.tips="环境探测器"
        /**
         * 默认查询16个继电器的状态
         */
        this.SearchStatusCode= "01030000000AC5CD"
    }

}
