package io.github.xiaolei.transaction.event;

/**
 * TODO: add comment
 */
public class ActionMenuItemClickEvent {
    public static final int ACTION_ITEM_NEW_PRODUCT = 1;

    private int clickMenuItemId;

    public int getClickMenuItemId(){
        return clickMenuItemId;
    }

    public ActionMenuItemClickEvent(int clickMenuItemId) {
        this.clickMenuItemId = clickMenuItemId;
    }
}
