package com.wusy.serialportproject.bean

import com.orhanobut.logger.Logger
import com.wusy.serialportproject.devices.Ate24V
import com.wusy.serialportproject.devices.BaseDevices
import com.wusy.serialportproject.devices.EnvQ3

import java.util.ArrayList
import java.util.HashMap

class EnvironmentalDetector(data: String, devices: BaseDevices) {
    var pM2_5: Int = 0//PM2.5
    var temp: Float = 0f//温度
    var humidity: Float = 0.toFloat()//湿度
    var cO2: Int = 0//CO2
    var pM2_5OutDoor: Int = 0//室外PM2.5
    var formaldehyde: Double = 0.toDouble()//甲醛
    var tvoc: Double = 0.toDouble()//TVOC
    var hexs: ArrayList<String>? = null
    var pM2_5Level:Int=0//PM2.5等级
    var AQI:Int=0
    init {
        when (devices.name) {
            "EnvQ3" -> {
                val map=(devices as EnvQ3).parseData(data)
                this.pM2_5 = map["PM2.5"] as Int
                this.temp = map["temp"] as Float
                this.humidity = map["humidity"] as Float
                this.cO2 = map["CO2"] as Int
                this.tvoc = map["TVOC"] as Double
                this.pM2_5OutDoor =map["PM2.5OutDoor"] as Int
                this.formaldehyde = map["formaldehyde"] as Double
                this.hexs = map["hexs"] as ArrayList<String>
            }
            "Ate24V"->{
                val map=(devices as Ate24V).parseData(data)
                this.pM2_5 = (map["PM2.5"]?:0) as Int
                this.temp = (map["temp"]?:0f) as Float
                this.humidity = (map["humidity"]?:0f) as Float
                this.cO2 = (map["CO2"]?:0 )as Int
                this.tvoc = (map["TVOC"]?:0.0 )as Double
                this.pM2_5Level = (map["PM2.5污染等级"] ?:0)as Int
                this.hexs = (map["hexs"]?:ArrayList<String>()) as ArrayList<String>
                this.AQI=(map["AQI"]?:0) as Int
            }
        }


    }
}
