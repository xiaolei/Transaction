package io.github.xiaolei.transaction.repository;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import io.github.xiaolei.transaction.entity.TransactionPhoto;

/**
 * TODO: add comment
 */
public class TransactionPhotoRepository extends BaseRepository {
    private Dao<TransactionPhoto, Long> transactionPhotoDao;

    public TransactionPhotoRepository(Context context) throws SQLException {
        super(context);

        transactionPhotoDao = getDataAccessObject(TransactionPhoto.class);
    }

    public TransactionPhoto queryForFirst(int transactionId, String photoUrl, int accountId) throws SQLException {
        QueryBuilder<TransactionPhoto, Long> queryBuilder = transactionPhotoDao.queryBuilder();
        PreparedQuery<TransactionPhoto> preparedQuery = queryBuilder.where().eq(TransactionPhoto.TRANSACTION_ID, transactionId).and()
                .eq(TransactionPhoto.PHOTO_URL, photoUrl).and()
                .eq(TransactionPhoto.ACCOUNT_ID, accountId).prepare();
        TransactionPhoto transactionPhoto = transactionPhotoDao.queryForFirst(preparedQuery);
        return transactionPhoto;
    }

    public void addPhoto(int transactionId, String photoUrl, int accountId) throws SQLException {
        TransactionPhoto transactionPhoto = queryForFirst(transactionId, photoUrl, accountId);
        if (transactionPhoto == null) {
            return;
        }

        TransactionPhoto newTransactionPhoto = new TransactionPhoto();
        newTransactionPhoto.setAccountId(accountId);
        newTransactionPhoto.setTransactionId(transactionId);
        newTransactionPhoto.setPhotoUrl(photoUrl);

        transactionPhotoDao.create(newTransactionPhoto);
    }

    public List<TransactionPhoto> query(int accountId, int transactionId) throws SQLException {
        QueryBuilder<TransactionPhoto, Long> queryBuilder = transactionPhotoDao.queryBuilder();
        PreparedQuery<TransactionPhoto> preparedQuery = queryBuilder.where().eq(TransactionPhoto.TRANSACTION_ID, transactionId).and()
                .eq(TransactionPhoto.ACCOUNT_ID, accountId).prepare();

        return transactionPhotoDao.query(preparedQuery);
    }
}
