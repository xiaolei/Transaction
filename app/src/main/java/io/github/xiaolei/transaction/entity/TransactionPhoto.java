package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "transaction_photo")
public class TransactionPhoto extends BaseEntity {
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String PHOTO_URL = "photo_url";

    @DatabaseField(canBeNull = false, columnName = TRANSACTION_ID)
    private int transactionId;

    @DatabaseField(canBeNull = false, columnName = PHOTO_URL)
    private String photoUrl;

    @DatabaseField(foreign = true, foreignColumnName = Transaction.ID)
    private Transaction transaction;

    public TransactionPhoto() {

    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
