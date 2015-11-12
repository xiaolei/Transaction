package io.github.xiaolei.enterpriselibrary.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

public class DialogHelper {
    /**
     * 显示 AlertDialog
     *
     * @param context
     * @param message
     * @param buttons
     */
    public static AlertDialog showAlertDialog(Context context, String title, String message, boolean cancellable,
                                              AlertDialogButton... buttons) {
        if (context == null || TextUtils.isEmpty(message)) {
            return null;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return null;
            }
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable)
                .create();

        if (buttons != null && buttons.length > 0) {
            for (int i = 0; i < buttons.length; i++) {
                AlertDialogButton button = buttons[i];
                alertDialog.setButton(button.WhichButton, button.Text,
                        button.ClickListener);
            }
        }
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 显示 AlertDialog
     *
     * @param context
     * @param message
     */
    public static AlertDialog showAlertDialog(Context context, String message) {
        AlertDialogButton button = new AlertDialogButton(
                AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return showAlertDialog(context, "提示", message, true, button);
    }

    /**
     * 显示 AlertDialog
     *
     * @param context
     * @param message
     */
    public static AlertDialog showAlertDialog(Context context, String message,
                                              DialogInterface.OnClickListener onOkButtonClickListener) {
        AlertDialogButton button = new AlertDialogButton(
                AlertDialog.BUTTON_POSITIVE, "确定", onOkButtonClickListener);
        return showAlertDialog(context, "提示", message, true, button);
    }
}
