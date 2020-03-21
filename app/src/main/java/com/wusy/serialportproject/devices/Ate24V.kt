package com.wusy.serialportproject.devices

import com.orhanobut.logger.Logger
import java.util.ArrayList

class Ate24V:BaseDevices(){
    init {
        this.name= "Ate24V"
        this.type="Env"
        this.tips="环境探测器"
        /**
         * 默认查询16个继电器的状态
         */
        this.SearchStatusCode= "01030000001705C4"
    }
    fun parseData(allData:String):HashMap<String,Any> {
        var data = allData.substring(6)
        var hexs = ArrayList<String>()
        for (i in 0 until data.length / 4) {
            hexs.add(data.substring(i * 4, i * 4 + 4))
        }
        var map=HashMap<String,Any>()
        map["hexs"]=hexs
        try {
            map["PM2.5"]= Integer.parseInt(hexs[9], 16)
            map["CO2"] = Integer.parseInt(hexs[10], 16)
            map["TVOC"]= Integer.parseInt(hexs[11], 16).toDouble()
            map["temp"]= (Integer.parseInt(hexs[12], 16).toFloat())/10
            map["humidity"]= Integer.parseInt(hexs[13], 16).toFloat()
            map["PM2.5污染等级"] = Integer.parseInt(hexs[22], 16)
            map["AQI"]=calcAQIByPM25(map["PM2.5"] as Int)
            Logger.i(
                "---------经Ate24V分析--------\n" +
                        "PM2.5=" + map["PM2.5"] + "ug/m3\n" +
                        "温度=" + map["temp"] + "℃\n" +
                        "湿度=" +  map["humidity"] + "%RH\n" +
                        "CO2=" +  map["CO2"] + "ppm\n" +
                        "TVOC=" +  map["TVOC"] + "\n" +
                        "PM2.5污染等级=" +map["PM2.5污染等级"] + "\n" +
                        "AQI=" +map["AQI"] + "\n" +
                        "-----------------------"
            )
        } catch (e: Exception) {
            Logger.e("解析环境数据发生错误", e)
        }
        return map
    }
    fun calcAQIByPM25(pm25:Int):Int{
        var aqi=0
        when(pm25){
            in 0..35->{
                aqi=50/35*(pm25-0)+0
            }
            in 35..75->{
                aqi=50/40*(pm25-35)+50
            }
            in 75..115->{
                aqi=50/40*(pm25-75)+100
            }
            in 115..150->{
                aqi=50/35*(pm25-115)+150
            }
            in 150..250->{
                aqi=100/100*(pm25-150)+200
            }
            in 250..350->{
                aqi=100/100*(pm25-250)+300
            }
            in 350..500->{
                aqi=100/150*(pm25-350)+400
            }
        }
        return aqi
    }
}

