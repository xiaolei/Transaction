package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class PopupMenuHelper {

    public static PopupMenu createPopupMenu(Context context, @MenuRes int menuResourceId, View anchorView,
                                            PopupMenu.OnMenuItemClickListener onMenuItemClickListener) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.getMenuInflater().inflate(menuResourceId, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

        return popupMenu;
    }
}
