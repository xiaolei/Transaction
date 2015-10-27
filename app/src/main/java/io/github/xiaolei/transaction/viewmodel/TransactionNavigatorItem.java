package io.github.xiaolei.transaction.viewmodel;

import io.github.xiaolei.transaction.adapter.TransactionNavigatorAdapter;

/**
 * TODO: add comment
 */
public class TransactionNavigatorItem {
    public TransactionFilterType transactionFilterType;
    public int iconResourceId;
    public int textResourceId;

    public TransactionNavigatorItem(TransactionFilterType transactionFilterType, int iconResourceId, int textResourceId) {
        this.transactionFilterType = transactionFilterType;
        this.iconResourceId = iconResourceId;
        this.textResourceId = textResourceId;
    }
}
