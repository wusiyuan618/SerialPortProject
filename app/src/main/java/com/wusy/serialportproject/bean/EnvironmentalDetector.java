package com.wusy.serialportproject.bean;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class EnvironmentalDetector {
    private int PM2_5;//PM2.5
    private float temp;//温度
    private float humidity;//湿度
    private int CO2;//CO2
    private int PM2_5OutDoor;//室外PM2.5
    private double formaldehyde;//甲醛
    private double TVOC;//TVOC
    ArrayList<String> hexs;

    public EnvironmentalDetector(String data){
        data=data.substring(6);
        hexs=new ArrayList<>();
        for (int i=0;i<data.length()/4;i++){
            hexs.add(data.substring(i*4,i*4+4));
        }
        try{
            this.PM2_5=Integer.parseInt(hexs.get(0),16);
            this.temp=((float)Integer.parseInt(hexs.get(1),16))/10;
            this.humidity=((float)Integer.parseInt(hexs.get(2),16))/10;
            this.CO2=Integer.parseInt(hexs.get(3),16);
            this.TVOC=((double)Integer.parseInt(hexs.get(4),16))/100;
            this.PM2_5OutDoor=Integer.parseInt(hexs.get(6),16);
            this.formaldehyde=((double)Integer.parseInt(hexs.get(7),16))/1000;
        }catch (Exception e){
            Logger.e("解析环境数据发生错误",e);
        }

    }

    public int getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(int PM2_5) {
        this.PM2_5 = PM2_5;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public int getCO2() {
        return CO2;
    }

    public void setCO2(int CO2) {
        this.CO2 = CO2;
    }

    public int getPM2_5OutDoor() {
        return PM2_5OutDoor;
    }

    public void setPM2_5OutDoor(int PM2_5OutDoor) {
        this.PM2_5OutDoor = PM2_5OutDoor;
    }

    public double getFormaldehyde() {
        return formaldehyde;
    }

    public void setFormaldehyde(double formaldehyde) {
        this.formaldehyde = formaldehyde;
    }

    public double getTVOC() {
        return TVOC;
    }

    public void setTVOC(double TVOC) {
        this.TVOC = TVOC;
    }

    public ArrayList<String> getHexs() {
        return hexs;
    }

    public void setHexs(ArrayList<String> hexs) {
        this.hexs = hexs;
    }
}
