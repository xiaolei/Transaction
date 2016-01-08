package io.github.xiaolei.transaction.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;
import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class InputDialogHelper {
    public static void show(final Context context, String title, String defaultValue,
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
