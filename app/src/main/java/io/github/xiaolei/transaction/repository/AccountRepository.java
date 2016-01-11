package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;

import io.github.xiaolei.transaction.database.DatabaseHelper;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.entity.Photo;

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

    public Account changeDisplayName(long accountId, String newDisplayName) throws SQLException {
        if (accountId <= 0 || TextUtils.isEmpty(newDisplayName)) {
            return null;
        }

        UpdateBuilder<Account, Long> updateBuilder = accountDao.updateBuilder();
        updateBuilder.updateColumnValue(Account.DISPLAY_NAME, new SelectArg(newDisplayName))
                .where().eq(Account.ID, accountId);
        accountDao.update(updateBuilder.prepare());

        return accountDao.queryForId(accountId);
    }

    public void changeDefaultCurrencyCode(Account account, String code) throws SQLException {
        if (account == null || TextUtils.isEmpty(code)) {
            return;
        }

        account.setDefaultCurrencyCode(code);
        accountDao.update(account);
    }

    public Account getAccountById(long accountId) throws SQLException {
        return accountDao.queryForId(accountId);
    }

    public Account updateAvatar(long accountId, String photoUrl) throws SQLException {
        if (accountId <= 0 || TextUtils.isEmpty(photoUrl)) {
            return null;
        }

        Photo photo = RepositoryProvider.getInstance(getContext()).resolve(PhotoRepository.class)
                .createPhoto(photoUrl, "", accountId);
        if(photo != null){
            UpdateBuilder<Account, Long> updateBuilder = accountDao.updateBuilder();
            updateBuilder.updateColumnValue(Account.PHOTO_ID, photo.getId())
                    .where().eq(Account.ID, accountId);
            accountDao.update(updateBuilder.prepare());
        }

        return getAccountById(accountId);
    }
}
