package io.github.xiaolei.transaction.viewmodel;

import io.github.xiaolei.transaction.viewholder.BaseViewHolder;

/**
 * TODO: add comment
 */
public class DashboardListItem implements BaseViewModel {
    public String label;
    public String value;

    public DashboardListItem(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
