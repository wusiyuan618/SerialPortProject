package com.wusy.serialportproject.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import com.wusy.serialportproject.R;
import com.wusy.wusylibrary.view.NumberKeyBoxView;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by XIAO RONG on 2019/1/4.
 */

public class NumberEditPopup extends BasePopupWindow {
    private TextView tvTitle,tvContent,tvEditContent;
    private NumberKeyBoxView numberKeyBoxView;
    public NumberEditPopup(Context context) {
        super(context);
        setBlurBackgroundEnable(true);
        findView();
        init();
    }
    private void findView(){
        tvTitle=getContentView().findViewById(R.id.tvTitle);
        tvContent=getContentView().findViewById(R.id.tvContent);
        tvEditContent=getContentView().findViewById(R.id.tvEditContent);
        numberKeyBoxView=getContentView().findViewById(R.id.numberkeyboxview);
    }
    private void init(){

    }
    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_numberedit);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public TextView getTvEditContent() {
        return tvEditContent;
    }

    public NumberKeyBoxView getNumberKeyBoxView() {
        return numberKeyBoxView;
    }
}
