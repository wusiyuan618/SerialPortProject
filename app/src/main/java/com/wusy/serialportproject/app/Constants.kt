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
        /*存储屏保的模式type：0=空气质量 1=模拟闹钟 2=数字闹钟 3=息屏*/
        const val SCREEN_SETTING_MODEL_TYPE: String = "screen_setting_model_type"
        /*存储代理商电话 */
        const val DEFAULT_DLS_PHONE = "default_dls_phone"
        /*节能温度 */
        const val DEFAULT_TEMP_JN_ZL = "default_temp_jn_zl"
        const val DEFAULT_TEMP_JN_ZR = "default_temp_jn_zr"

        /*离家温度 */
        const val DEFAULT_TEMP_OUTHOME_ZL = "default_temp_outhome_zl"
        const val DEFAULT_TEMP_OUTHOME_ZR = "default_temp_outhome_zr"

        /* 通风时间 */
        const val DEFAULT_SETTING_TFTIME = "default_setting_tftime"

        /* 存储用户设置的适宜温度 */
        const val ENJOYTEMP="enjoy_temp"
        /* 存储通风是否开启定时 */
        const val ISOPEN_XFTIME="is_open_xftime"

        /*存储空调首页按钮的最后状态*/
        const val BTN_STATE_LN="btn_state_ln"
        const val BTN_STATE_SD="btn_state_sd"
        const val BTN_STATE_XF="btn_state_xf"
        const val BTN_STATE_SWITCH="btn_state_switch"


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
        /* 温度的默认值 */
        const val DEFAULT_TEMPDATA_JNZL = "26℃"
        const val DEFAULT_TEMPDATA_JNZR = "18℃"
        const val DEFAULT_TEMPDATA_LJZL = "28℃"
        const val DEFAULT_TEMPDATA_LJZR = "16℃"
        /* 默认通风时间 */
        const val DEFAULT_TF_TIME = "15分钟"

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