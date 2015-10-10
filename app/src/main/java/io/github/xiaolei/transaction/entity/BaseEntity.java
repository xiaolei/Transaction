package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;

/**
 * TODO: add comment
 */
public class BaseEntity extends TableEntity {
    public static final String ACCOUNT_ID = "account_id";

    @DatabaseField(foreign = true, columnName = "account_id")
    private Account account;

    @DatabaseField(columnName = "account_id")
    private long accountId;

    public BaseEntity() {

    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        this.accountId = account.getId();
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
