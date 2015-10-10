package io.github.xiaolei.transaction;

import android.app.Application;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import de.greenrobot.event.EventBus;
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
    }

    protected void initialize() {
        Task.callInBackground(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    initializeAccountInfo();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }).continueWith(new Continuation<Boolean, Boolean>() {
            @Override
            public Boolean then(Task<Boolean> task) throws Exception {
                boolean success = task.getResult();
                if (success) {
                    notifyAccountInfoLoadCompleted();
                }

                notifyApplicationInitializationCompleted(task.getResult());

                return success;
            }
        }, Task.UI_THREAD_EXECUTOR);
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
