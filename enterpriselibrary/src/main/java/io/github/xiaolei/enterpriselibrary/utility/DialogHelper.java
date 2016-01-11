package io.github.xiaolei.enterpriselibrary.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import io.github.xiaolei.enterpriselibrary.R;
import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;

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
        if (context == null || TextUtils.isEmpty(message)) {
            return null;
        }

        AlertDialogButton button = new AlertDialogButton(
                AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return showAlertDialog(context, "", message, true, button);
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
        return showAlertDialog(context, "", message, true, button);
    }

    public static AlertDialog showConfirmDialog(Context context, String message, DialogInterface.OnClickListener onYesButtonClickListener) {
        AlertDialogButton yesButton = new AlertDialogButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.yes), onYesButtonClickListener);
        AlertDialogButton noButton = new AlertDialogButton(
                AlertDialog.BUTTON_NEGATIVE, context.getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return showAlertDialog(context, "", message, true, yesButton, noButton);
    }

    public static void showInputDialog(final Context context, String title, String defaultValue,
                            final OnOperationCompletedListener<String> onOperationCompletedListener) {
        if (context == null) {
            return;
        }

        final View view = View.inflate(context, R.layout.dialog_fragment_input, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.editTextProductName.setText(defaultValue);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideSoftKeyboard(context, view);
                        if (onOperationCompletedListener != null) {
                            onOperationCompletedListener.onOperationCompleted(true, viewHolder.editTextProductName.getText().toString(), null);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                hideSoftKeyboard(context, view);
                                dialog.dismiss();
                            }
                        }
                )
                .create();
        dialog.show();
    }

    public static void hideSoftKeyboard(Context context, View focusedView) {
        if (focusedView != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private static class ViewHolder {
        public EditText editTextProductName;

        public ViewHolder(View view) {
            editTextProductName = (EditText) view.findViewById(io.github.xiaolei.enterpriselibrary.R.id.editTextProductName);
        }
    }
}
