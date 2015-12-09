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

    @DatabaseField(canBeNull = false, columnName = TRANSACTION_ID)
    private long transactionId;

    @DatabaseField(canBeNull = false, columnName = PHOTO_ID)
    private long photoId;

    @DatabaseField(foreign = true, foreignColumnName = Transaction.ID)
    private Transaction transaction;

    @DatabaseField(foreign = true, foreignColumnName = Photo.ID)
    private Photo photo;

    public TransactionPhoto() {

    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
