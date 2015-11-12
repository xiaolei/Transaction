package io.github.xiaolei.enterpriselibrary.utility;

import android.content.DialogInterface.OnClickListener;

public class AlertDialogButton {
	public int WhichButton;
	public String Text;
	public OnClickListener ClickListener;

	public AlertDialogButton(int whichButton, String text,
							 OnClickListener clickListener) {
		this.WhichButton = whichButton;
		this.Text = text;
		this.ClickListener = clickListener;
	}
}
