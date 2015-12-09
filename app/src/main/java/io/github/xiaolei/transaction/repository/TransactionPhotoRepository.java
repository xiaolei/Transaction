package io.github.xiaolei.transaction.repository;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import io.github.xiaolei.transaction.entity.Photo;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.entity.TransactionPhoto;

/**
 * TODO: add comment
 */
public class TransactionPhotoRepository extends BaseRepository {
    private Dao<TransactionPhoto, Long> transactionPhotoDao;
    private Dao<Photo, Long> photoDao;

    public TransactionPhotoRepository(Context context) throws SQLException {
        super(context);

        transactionPhotoDao = getDataAccessObject(TransactionPhoto.class);
        photoDao = getDataAccessObject(Photo.class);
    }

    public Transaction addPhoto(final long transactionId, final String photoUrl, final String photoDescription, final long accountId) throws SQLException {
        Transaction result = TransactionManager.callInTransaction(getDatabase().getConnectionSource(), new Callable<Transaction>() {

            @Override
            public Transaction call() throws Exception {
                Photo photo = RepositoryProvider.getInstance(getContext()).resolve(PhotoRepository.class)
                        .createPhoto(photoUrl, photoDescription, accountId);

                TransactionPhoto newTransactionPhoto = new TransactionPhoto();
                newTransactionPhoto.setAccountId(accountId);
                newTransactionPhoto.setTransactionId(transactionId);
                newTransactionPhoto.setPhotoId(photo.getId());

                return RepositoryProvider.getInstance(getContext()).resolve(TransactionRepository.class)
                        .getTransactionById(transactionId);
            }
        });

        return result;
    }

    public List<TransactionPhoto> query(int accountId, int transactionId) throws SQLException {
        QueryBuilder<TransactionPhoto, Long> queryBuilder = transactionPhotoDao.queryBuilder();
        PreparedQuery<TransactionPhoto> preparedQuery = queryBuilder.where().eq(TransactionPhoto.TRANSACTION_ID, transactionId).and()
                .eq(TransactionPhoto.ACCOUNT_ID, accountId).prepare();

        return transactionPhotoDao.query(preparedQuery);
    }
}
