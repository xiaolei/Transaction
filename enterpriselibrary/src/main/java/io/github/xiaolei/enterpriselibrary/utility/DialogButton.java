package io.github.xiaolei.enterpriselibrary.utility;


import android.app.Dialog;

import io.github.xiaolei.enterpriselibrary.listener.OnDialogButtonClickListener;


public class DialogButton {
    public String Text;
    public OnDialogButtonClickListener ClickListener;
    public Dialog dialog;

    public DialogButton(String text,
                        OnDialogButtonClickListener clickListener) {
        this.Text = text;
        this.ClickListener = clickListener;
    }
}
