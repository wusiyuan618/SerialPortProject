package com.wusy.serialportproject.proxy;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by XIAO RONG on 2018/10/24.
 */

public class ClickProxy implements View.OnClickListener{
    private static boolean isStartSingleClick=false;
    private View.OnClickListener onClickListener;
    private Context mC;
    private long lastClickTime=0;
    private long times=1000;
    public ClickProxy(View.OnClickListener onClickListener, Context context){
        this.onClickListener=onClickListener;
        this.mC=context;
    }


    @Override
    public void onClick(View v) {
        if(isStartSingleClick){
            if (System.currentTimeMillis() - lastClickTime >= times) {
                lastClickTime = System.currentTimeMillis();
            }else{
                Toast.makeText(mC,"您点得太快了",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        onClickListener.onClick(v);
    }
}
