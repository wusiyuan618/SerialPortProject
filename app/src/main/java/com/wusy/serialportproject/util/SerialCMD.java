package com.wusy.serialportproject.util;

public class SerialCMD {
    /**
     * 查询环境探测器数据
     * 从第0个寄存器开始读，读10个寄存器
     */
    public final static String EnvironmenttalSearch="01030000000AC5CD";
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
    public final static String JDQSearch="AA550400030664000D0A";
    /**
     * 寄电器控制码
     *  code: 控制寄电器连点。二进制对应寄电器上的灯，1为亮。
     *  例如：00111111 即前6个灯亮，转为Hex为3f即code=3f
     */
    public static String getSendControlCode(String code){
        return "AA550400031E"+code+"FF0D0A";
    }
    /**
     * ------------------------------------------------
     * ZZ-IO1600寄电器命令
     * ------------------------------------------------
     */
    /**
     * 获取发送控制命令
     * @param index 对应的第几个寄电器
     */
    public static String getZZSendControlCode(int index,boolean isOpen){
        String star="FE";
        String address="05";
        String indexStr="";
        if(index<16) indexStr="000"+Integer.toHexString(index);
        else indexStr="0010";//最多16个
        String openStr="";
        if(isOpen) openStr="FF00";
        else openStr="0000";
        return star+address+indexStr+openStr+DataUtils.getCRC(star+address+indexStr+openStr);
    }

    /**
     * 默认查询16个继电器的状态
     * @return
     */
    public static String getZZSendSearchCode(){
        return "FE0100000010"+DataUtils.getCRC("FE0100000010");
    }
}
