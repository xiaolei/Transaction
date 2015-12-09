package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;

/**
 * TODO: add comment
 */
public class BaseEntity extends TableEntity {
    public static final String ACCOUNT_ID = "account_id";

    @DatabaseField(columnName = "account_id")
    private long accountId;

    public BaseEntity() {

    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
