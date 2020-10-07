package com.playplus.app.smswatcher.aaa;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by wayne on 2017/11/21.
 * <p>
 * 全版 Loading Dialog
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.FullDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        // 全版面的Dialog
        setFullScreenDialog(this);
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    /**
     * 將Dialog設定為全版
     */
    public void setFullScreenDialog(Dialog dialog) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = metrics.widthPixels;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = metrics.heightPixels - (int)ViewUtils.convertDpToPixel(24,dialog.getContext());
            win.setAttributes(lp);

            win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE |
                    WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
