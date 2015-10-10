package io.github.xiaolei.transaction.repository;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import io.github.xiaolei.transaction.database.DatabaseHelper;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.entity.TableEntity;

/**
 * Base repository. Write business logic in the repository class.
 */
public abstract class BaseRepository {
    private Context mContext;

    public BaseRepository(Context context) {
        mContext = context;
    }

    public synchronized <T extends TableEntity> Dao<T, Long> getDataAccessObject(Class<T> type) throws SQLException {
        return DatabaseHelper.getInstance(mContext).getDataAccessObject(type);
    }

    public DatabaseHelper getDatabase() {
        return DatabaseHelper.getInstance(getContext());
    }

    public Context getContext() {
        return mContext;
    }
}
