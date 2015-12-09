package io.github.xiaolei.transaction.viewmodel;

import android.content.DialogInterface;
import android.view.View;

/**
 * TODO: add comment
 */
public class ActionButtonInfo {
    /**
     * {@link ActionButtonId}
     */
    public int id;
    public String text;
    public int iconResourceId;

    public ActionButtonInfo(int id, String text, int iconResourceId) {
        this.id = id;
        this.text = text;
        this.iconResourceId = iconResourceId;
    }
}
