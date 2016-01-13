package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.event.DefaultCurrencyCodeChangedEvent;
import io.github.xiaolei.transaction.repository.AccountRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;

/**
 * TODO: add comment
 */
public class ConfigurationManager {
    public static final int DECIMAL_POINT_LEFT = 2;
    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final String PICTURES_FOLDER_NAME = "Transaction";
    private static final String TAG = ConfigurationManager.class.getSimpleName();

    public static String getPictureFolderPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath().toString() + File.separator + PICTURES_FOLDER_NAME;
    }

    public static void changeDefaultCurrencyCode(final Context context, final String currencyCode) {
        if (TextUtils.isEmpty(currencyCode) || GlobalApplication.getCurrentAccount() == null) {
            return;
        }

        final Account currentAccount = GlobalApplication.getCurrentAccount();
        currentAccount.setDefaultCurrencyCode(currencyCode);
        AsyncTask<Void, Void, Exception> task = new AsyncTask<Void, Void, Exception>() {

            @Override
            protected Exception doInBackground(Void... voids) {
                Exception result = null;
                try {
                    RepositoryProvider.getInstance(context).resolve(AccountRepository.class).save(currentAccount);
                } catch (SQLException e) {
                    result = e;
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                return result;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result == null) {
                    EventBus.getDefault().post(new DefaultCurrencyCodeChangedEvent(currencyCode));
                }
            }
        };
        task.execute();
    }
}
