package com.wusy.serialportproject.util;

import android.os.Handler;
import android.os.Message;
import com.orhanobut.logger.Logger;
import com.wits.serialport.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author by AllenJ on 2018/4/20.
 * <p>
 * 通过串口用于接收或发送数据
 */

public class SerialPortUtil {

    private SerialPort serialPort = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private ReceiveThread mReceiveThread = null;
    private boolean isStart = false;
    private Handler handler = null;

    public SerialPortUtil(Handler handler) {
        this.handler = handler;
    }
    private final String PATH = "/dev/ttyS2";
    private final int BAUDRATE = 9600;

    /**
     * 打开串口，接收数据
     * 通过串口，接收单片机发送来的数据
     */
    public void openSerialPort(String path,int baudrate) {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0);
            //调用对象SerialPort方法，获取串口中"读和写"的数据流
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            isStart = true;
        } catch (IOException e) {
            Logger.e(e,"openSerialPort()");
            e.printStackTrace();
        }
        getSerialPort();
    }

    /**
     * 关闭串口
     * 关闭串口中的输入输出流
     */
    public void closeSerialPort() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     *
     * @param data 要发送的数据
     */
    public void sendSerialPort(String data) {
        try {
            byte[] sendData = DataUtils.HexString2Bytes(data);
            outputStream.write(sendData);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSerialPort() {
        if (mReceiveThread == null) {
            mReceiveThread = new ReceiveThread();
        }
        mReceiveThread.start();
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();
            //条件判断，只要条件为true，则一直执行这个线程
            while (isStart) {
                if (inputStream == null) {
                    return;
                }
                try {
                    Thread.sleep(500);
                    byte[] readData = new byte[1024];
                    int size = inputStream.read(readData);
                    if (size > 0) {
                        String readString = DataUtils.byteToHex(readData, size).replace("\n", "");
                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = readString;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Logger.e( e,"ReceiveThread");
                }
            }
        }
    }
}
