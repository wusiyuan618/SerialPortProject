package com.wusy.serialportproject.ui.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import com.wusy.serialportproject.R;
import com.wusy.serialportproject.bean.EnvironmentalDetector;
import com.wusy.serialportproject.devices.EnvQ3;
import com.wusy.serialportproject.ui.EnvAirActivity;
import com.wusy.serialportproject.util.CommonConfig;

import java.util.ArrayList;

public class SerialAirActivity extends Activity {
    private TextView tv_log;
    private EditText ed_send,ed_jdqControl;
    private Button btn_send,btn_back,btn_searchEnv,btn_jdqControl,btn_jdqSearchStatus;
    private SerialAirBoradCast boradCast;
    private IntentFilter intentFilter;
    private StringBuffer buffer=new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_serial_air);
        findView();
        init();
    }
    private void findView(){
        ed_send=findViewById(R.id.ed_send);
        ed_jdqControl=findViewById(R.id.ed_jdqControl);

        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_send=findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSerial(ed_send.getText().toString());
            }
        });
//        btn_searchEnv=findViewById(R.id.btn_searchEnv);
//        btn_searchEnv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSerial(SerialCMD.EnvironmenttalSearch);
//            }
//        });
//        btn_jdqControl=findViewById(R.id.btn_jdqControl);
//        btn_jdqControl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSerial(SerialCMD.getSendControlCode(ed_jdqControl.getText().toString()));
//            }
//        });
//        btn_jdqSearchStatus=findViewById(R.id.btn_jdq_searchStatus);
//        btn_jdqSearchStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSerial(SerialCMD.JDQSearch);
//            }
//        });

        tv_log=findViewById(R.id.tv_log);
    }
    private void init(){
        boradCast=new SerialAirBoradCast();
        intentFilter=new IntentFilter();
        intentFilter.addAction(CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI);
        registerReceiver(boradCast, intentFilter);
        tv_log.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    private void  logOnTv(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.i(msg+"\n");
                tv_log.append(msg+"\n");
                int offset=tv_log.getLineCount()*tv_log.getLineHeight();
                if(offset>tv_log.getHeight()){
                    tv_log.scrollTo(0,offset-tv_log.getHeight()+tv_log.getLineHeight()*2);
                }
            }
        });
    }
    private void sendSerial(String msg){
        Intent intent=new Intent();
        intent.putExtra("data","send");
        intent.putExtra("msg",msg);
        logOnTv("send serial broadcat msg="+msg);
        intent.setAction(CommonConfig.SERIALPORTPROJECT_ACTION_SP_SERVICE);
        sendBroadcast(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(boradCast!=null) unregisterReceiver(boradCast);

    }
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://环境检测仪获取到的数据
                    logOnTv("获取的环境检测仪的数据"+msg.obj);
                    EnvironmentalDetector enD=new EnvironmentalDetector(msg.obj.toString(), new EnvQ3());
                    logOnTv("---------经分析--------\n"+
                            "PM2.5="+enD.getPM2_5()+"\n"+
                            "温度="+enD.getTemp()+"\n"+
                            "湿度="+enD.getHumidity()+"\n"+
                            "CO2="+enD.getCO2()+"\n"+
                            "TVOC="+enD.getTvoc()+"\n"+
                            "室外PM2.5="+enD.getPM2_5OutDoor()+"\n"+
                            "甲醛="+enD.getFormaldehyde()+"\n"+
                            "-----------------------");
                    break;
                case 1:
                    logOnTv("获取的寄电器状态的数据"+msg.obj);
                    String data=msg.obj.toString().substring(16,18);
                    logOnTv(getStatus(data,8).toString());
                    break;
            }
        }
    };
    private ArrayList<Integer> getStatus(String data, int len){
        ArrayList<Integer> list=new ArrayList();
        for (int i=0;i<len;i++){
            list.add((Integer.parseInt(data,16) >> i) & 1);
        }
        return list;
    }
    class SerialAirBoradCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI)){
                String data = intent.getStringExtra("msg");
                logOnTv("get SerialPort msg by BroadCast-HJL_ACTION_SERIALPORT_MSG="+data);
                buffer.append(data);
                if(buffer.toString().length()>4&&buffer.toString().substring(0,4).equals("0103")){//环境探测器数据
                    Message message=Message.obtain();
                    message.what=0;
                    message.obj=buffer.toString();
                    handler.sendMessage(message);
                    buffer.delete(0,buffer.length());
                }
                if(buffer.toString().length()>4&&
                        buffer.toString().substring(0,4).toLowerCase().equals("AA55".toLowerCase())&&
                            buffer.toString().substring(buffer.length()-4,buffer.length()).toLowerCase().equals("0D0A".toLowerCase())){//寄电器状态
                    Message message=Message.obtain();
                    message.what=1;
                    message.obj=buffer.toString();
                    handler.sendMessage(message);
                    buffer.delete(0,buffer.length());
                }
                if(buffer.length()>1024){//防止出血莫名其妙的问题，清空下buffer
                    buffer.delete(0,buffer.length());
                }
            }
        }
    }

}
