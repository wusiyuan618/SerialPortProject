package com.wusy.serialportproject.util;

public class SerialCMD {
    /**
     * 查询环境探测器数据
     * 从第0个寄存器开始读，读10个寄存器
     */
    public final static String EnvironmenttalSearch="01030000000AC5CD";
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
}
