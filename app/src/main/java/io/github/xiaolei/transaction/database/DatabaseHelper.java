package io.github.xiaolei.transaction.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import io.github.xiaolei.enterpriselibrary.utility.FileUtils;
import io.github.xiaolei.transaction.common.ContextReference;
import io.github.xiaolei.transaction.entity.TableEntity;

/**
 * A helper to manager database
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String TAG = DatabaseHelper.class.getSimpleName();
    private static DatabaseHelper mInstance;
    private static ContextReference mContextReference;

    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_FILE_FOLDER = "sql";
    private static final String SQL_FILE_CREATE = "create.sql";
    private static final String SQL_FILE_UPGRADE = "upgrade_from_version_%dto%d.sql";

    private static HashMap<Class<? extends TableEntity>, Object> mDaoInstances;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns the Database Access Object (DAO) for the specified entity class. It will create it or just give the cached
     * instance.
     *
     * @param entityType
     * @param <T>
     * @return
     * @throws SQLException
     */
    public synchronized <T extends TableEntity> Dao<T, Long> getDataAccessObject(Class<T> entityType) throws SQLException {
        if (mDaoInstances.containsKey(entityType)) {
            return (Dao<T, Long>) mDaoInstances.get(entityType);
        }

        Dao<T, Long> result = DaoManager.createDao(this.getConnectionSource(), entityType);
        mDaoInstances.put(entityType, result);
        return result;
    }


    /**
     * Gets the singleton instance of the database helper.
     *
     * @param context
     * @return
     */
    public synchronized static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mContextReference = new ContextReference(context);
            mInstance = new DatabaseHelper(mContextReference.context);
        }
        if (mDaoInstances == null) {
            mDaoInstances = new HashMap<Class<? extends TableEntity>, Object>();
        }

        mContextReference.context = context;

        return mInstance;
    }

    /**
     * Checks whether the specified file name exists in "assets" folder.
     *
     * @param fileName
     * @return
     */
    private boolean fileExists(String fileName) {
        try {
            String[] fileNames = mContextReference.context.getAssets().list(SQL_FILE_FOLDER);
            boolean result = Arrays.asList(mContextReference.context.getAssets().list(SQL_FILE_FOLDER))
                    .contains(fileName);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Executes sql script in the specified sql file name line by line.
     *
     * @param db
     * @param sqlFileName
     */
    private void executeSqlFileLineByLine(SQLiteDatabase db, String sqlFileName) {
        if (TextUtils.isEmpty(sqlFileName)) {
            return;
        }

        if (!fileExists(sqlFileName)) {
            Log.w(TAG, String.format("Cannot find sql file: %s", sqlFileName));
            return;
        }

        sqlFileName = SQL_FILE_FOLDER + File.separator + sqlFileName;

        db.beginTransaction();
        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(mContextReference.context.getAssets().open(sqlFileName)));
            try {
                String sql = null;
                while ((sql = reader.readLine()) != null) {
                    if (!TextUtils.isEmpty(sql) && sql.trim().length() > 0 && !sql.startsWith("//")) {
                        db.execSQL(sql);
                        Log.i(TAG, "[" + sqlFileName + "] " + "Execute sql: " + sql);
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            // Commit
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Deletes the database of the specified context.
     *
     * @param context
     */
    public static void deleteDatabase(Context context) {
        if (context != null) {
            DatabaseHelper.getInstance(context).close();
            context.getDatabasePath(DATABASE_NAME).getAbsoluteFile().delete();
        }
    }

    public void executeSql(String... sqlLines) {
        if(sqlLines == null || sqlLines.length == 0){
            return;
        }

        for(String sql: sqlLines) {
            getWritableDatabase().execSQL(sql);
        }
    }

    public void executeSql(String sql, Objects[] bindArgs) {
        if(TextUtils.isEmpty(sql)){
            return;
        }

        getWritableDatabase().execSQL(sql, bindArgs);
    }

    public File backup() throws IOException {
        File targetFile = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + DATABASE_NAME);
        targetFile.createNewFile();
        File dbFile = mContextReference.context.getDatabasePath(DATABASE_NAME).getAbsoluteFile();
        FileUtils.copyToFile(dbFile, targetFile);

        return targetFile;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        Log.i(TAG, String.format("onCreate. Create database at path:", db.getPath()));
        this.executeSqlFileLineByLine(db, SQL_FILE_CREATE);
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        for (int version = oldVersion; version < newVersion; version++) {
            String sqlFileName = String.format(Locale.getDefault(),
                    SQL_FILE_UPGRADE, version, newVersion);
            Log.i(TAG, String.format(
                    "Upgrade database from oldVersion: %d to newVersion: %d",
                    version, newVersion));
            executeSqlFileLineByLine(db, sqlFileName);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }
}