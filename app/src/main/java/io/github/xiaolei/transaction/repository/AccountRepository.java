package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import io.github.xiaolei.transaction.database.DatabaseHelper;
import io.github.xiaolei.transaction.entity.Account;

/**
 * TODO: add comment
 */
public class AccountRepository extends BaseRepository {
    private Dao<Account, Long> accountDao;

    public AccountRepository(Context context) throws SQLException {
        super(context);

        accountDao = getDataAccessObject(Account.class);
    }

    public Dao<Account, Long> getAccountDao() {
        return accountDao;
    }

    public Dao.CreateOrUpdateStatus save(Account account) throws SQLException {
        Dao.CreateOrUpdateStatus result = accountDao.createOrUpdate(account);
        accountDao.refresh(account);

        return result;
    }

    public void changeDefaultCurrencyCode(Account account, String code) throws SQLException {
        if (account == null || TextUtils.isEmpty(code)) {
            return;
        }

        account.setDefaultCurrencyCode(code);
        accountDao.update(account);
    }
}
