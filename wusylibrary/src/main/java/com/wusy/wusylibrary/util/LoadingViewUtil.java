package com.wusy.wusylibrary.util;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.wusy.wusylibrary.R;

/**
 * Created by XIAO RONG on 2018/4/25.
 */

public class LoadingViewUtil {
    private static LoadingViewUtil loadingViewUtil;
    private LoadingViewUtil(){

    }
    public synchronized static LoadingViewUtil getInstance(){
        if (loadingViewUtil==null) loadingViewUtil=new LoadingViewUtil();
        return loadingViewUtil;
    }

    /**
     * 创建一个加载动画
     * @param msg 加载文字
     * @param isClick 是否可点击窗口意外的内容
     * @return
     */
    public Dialog createLoadingDialog(Context context,String msg,boolean isClick){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_loading_view);// 加载布局
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);// 创建自定义样式dialog
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(isClick); // 点击加载框以外的区域
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        return loadingDialog;
    }

    /**
     * 创建一个加载动画，默认不可点击窗口以外的内容
     * @param msg
     * @return
     */
    public Dialog createLoadingDialog(Context context,String msg){
        return createLoadingDialog(context,msg,false);
    }

    /**
     * 显示指定Dialog
     * @param dialog
     */
    public void showDialog(Dialog dialog){
        if(dialog!=null){
            try {
                dialog.show();
            }catch (Exception  e){
                LogUtil.e("LoadingViewUtil","显示加载动画发生了异常"+e.getLocalizedMessage());
            }
        }
    }


    /**
     * 关闭指定dialog
     * @param dialog
     */
    public void dismissDialog(Dialog dialog){
        try {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        }catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("LoadingViewUtil","关闭加载动画发生了异常"+e.getLocalizedMessage());
        }
    }
}
