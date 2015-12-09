package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "transaction_photo")
public class TransactionPhoto extends BaseEntity {
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String PHOTO_ID = "photo_id";

    @DatabaseField(foreign = true, columnName = TRANSACTION_ID, foreignColumnName = Transaction.ID)
    private Transaction transaction;

    @DatabaseField(foreign = true, columnName = PHOTO_ID, foreignColumnName = Photo.ID)
    private Photo photo;

    public TransactionPhoto() {

    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
