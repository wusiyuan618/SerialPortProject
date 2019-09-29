package com.wusy.serialportproject.app

import com.wusy.serialportproject.bean.EnvironmentalDetector
import java.util.*

class Constants {
    companion object {
        /**
         * --------------------共享参数--------------------
         */
        /*存储屏保开启间隔时间 */
        const val SCREEN_SETTING_TIME: String = "screen_setting_time"
        /*存储屏保开启间隔时间在WheelView中的posistion */
        const val SCREEN_SETTING_POSITION: String = "screen_setting_position"
        /*存储新风开启间隔时间 */
        const val FRESH_SETTING_TIME: String = "fresh_setting_time"
        /*存储新风开启间隔时间在WheelView中的posistion */
        const val FRESH_SETTING_POSITION: String = "fresh_setting_position"
        /*存储代理商电话 */
        const val DEFAULT_DLS_PHONE = "default_dls_phone"
        /**
         * -------------------------------------------------
         */


        /**
         *  --------------------默认值--------------------
         */
        /* 系统设置权限密码 */
        const val DEFAULT_SYSTEM_SETTINGPWD = "667788"
        /* 代理商默认电话 */
        const val DEFAULT_DLS_PHONENUMBER = "18756568778"
        /*屏保打开默认时间。修改默认时间记得去吧屏保设置的默认position改了*/
        const val DEFAULT_SCREEN_DISTANCE_TIME = "30分钟"
        /**
         * -----------------------------------------------
         */

        /**
         * --------------------屏保相关--------------------
         */
        /*屏保是否打开*/
        var isOpenScreen = false
        /*上次触摸屏幕的时间*/
        var lastUpdateTime: Date = Date(System.currentTimeMillis())
        /*当前的空气质量信息*/
        var curED:EnvironmentalDetector?=null
        /**
         * ------------------------------------------------
         */
    }
}