package com.wusy.serialportproject.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import com.wusy.serialportproject.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by XIAO RONG on 2019/1/4.
 */

public class MakeSurePopup extends BasePopupWindow {
    private TextView tvTitle,tvContent;
    private ImageView ivSure,ivCancel;
    public MakeSurePopup(Context context) {
        super(context);
        setBlurBackgroundEnable(true);
        findView();
        init();
    }
    private void findView(){
        tvTitle=getContentView().findViewById(R.id.tvTitle);
        tvContent=getContentView().findViewById(R.id.tvContent);
        ivSure=getContentView().findViewById(R.id.ivSure);
        ivCancel=getContentView().findViewById(R.id.ivCancel);
    }
    private void init(){
        ivCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_makesure);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultAlphaAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultAlphaAnimation(false);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public ImageView getIvSure() {
        return ivSure;
    }

    public ImageView getIvCancel() {
        return ivCancel;
    }
}
