package com.wusy.serialportproject.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import com.wusy.serialportproject.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by XIAO RONG on 2019/1/4.
 */

public class NumberBoxPopup extends BasePopupWindow {
    public NumberBoxPopup(Context context) {
        super(context);
        setBlurBackgroundEnable(true);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_numberkeybox);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }
}
