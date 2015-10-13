package io.github.xiaolei.transaction;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.event.AccountInfoLoadCompletedEvent;
import io.github.xiaolei.transaction.event.AppInitCompletedEvent;
import io.github.xiaolei.transaction.repository.AccountRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.util.FileUtils;
import io.github.xiaolei.transaction.util.PreferenceHelper;

/**
 * TODO: add comment
 */
public class GlobalApplication extends Application {
    private static final String TAG = GlobalApplication.class.getSimpleName();
    private static Account currentAccount;
    private static boolean mIsInitialized;

    public static boolean isInitialized() {
        return mIsInitialized;
    }


    public static Account getCurrentAccount() {
        return currentAccount;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void notifyAccountInfoLoadCompleted() {
        EventBus.getDefault().post(new AccountInfoLoadCompletedEvent());
    }

    public void notifyApplicationInitializationCompleted(boolean success) {
        mIsInitialized = true;
        EventBus.getDefault().post(new AppInitCompletedEvent(success));
        Logger.d(TAG, "Application initialized completed");
    }

    protected void initialize() {
        Logger.setEnable(GlobalConfiguration.DEBUG);
        Logger.d(TAG, "Application initializing...");

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    initializeAccountInfo();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    notifyAccountInfoLoadCompleted();
                }

                notifyApplicationInitializationCompleted(result);
            }
        };
        task.execute();
    }

    /**
     * Create a new account or load current account
     */
    private void initializeAccountInfo() throws SQLException {
        final long accountId = PreferenceHelper.getInstance(getApplicationContext()).getSharedPreferences().getLong(PreferenceHelper.PREF_KEY_CURRENT_ACCOUNT_ID, -1);
        Log.d(TAG, "saved account id: " + accountId);

        if (accountId <= 0) {
            currentAccount = createAccount();
        } else {
            Dao<Account, Long> accountDao = RepositoryProvider.getInstance(getApplicationContext()).resolve(AccountRepository.class).getDataAccessObject(Account.class);
            currentAccount = accountDao.queryForId(accountId);
            if (currentAccount != null) {
                Log.d(TAG, "load current account. id = " + accountId);
            } else {
                createAccount();
            }
        }
    }

    private void createPictureFolder() {
        if (FileUtils.isExternalStorageAvailable()) {
            FileUtils.createFolderPathInPicturesFolder(ConfigurationManager.PICTURES_FOLDER_NAME);
        }
    }

    private Account createAccount() throws SQLException {
        Account account = new Account("New User", "newuser@example.com", "");
        account.setDefaultCurrencyCode("USD");
        RepositoryProvider.getInstance(getApplicationContext()).resolve(AccountRepository.class).save(account);
        PreferenceHelper.getInstance(getApplicationContext()).setPreferenceValue(PreferenceHelper.PREF_KEY_CURRENT_ACCOUNT_ID, account.getId());
        return account;
    }
}
