package com.suman.news_hound.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by sumansucharitdas on 5/18/16.
 */
public class ViewUtils {

    // Set translucent status for api below 19, material design
    @TargetApi(19) public void setTranslucentStatus(boolean on, Activity localActivity) {
        Window win = localActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
