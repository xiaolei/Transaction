package io.github.xiaolei.enterpriselibrary.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.github.xiaolei.enterpriselibrary.R;
import io.github.xiaolei.enterpriselibrary.logging.Logger;

/**
 * Helper class to manager the file downloader workers.
 */
public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    private Context mContext;
    private static DownloadManager INSTANCE;
    protected LinkedHashMap<String, DownloadWorker> mDownloadWorkers;

    protected DownloadManager() {
        mDownloadWorkers = new LinkedHashMap<>();
    }

    public synchronized static DownloadManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DownloadManager();
        }

        INSTANCE.mContext = context;

        return INSTANCE;
    }

    public Context getContext() {
        return mContext;
    }

    public void download(String fileUrl, String targetFileName, DownloaderCallback callback) {
        if (TextUtils.isEmpty(fileUrl)) {
            callback.onFailure(fileUrl, getContext().getString(R.string.error_file_name_is_empty));
            return;
        }

        if (mDownloadWorkers.containsKey(fileUrl.toLowerCase().trim())) {
            callback.onFailure(fileUrl, getContext().getString(R.string.error_file_is_downloading, fileUrl));
            return;
        }

        fileUrl = fileUrl.toLowerCase().trim();
        DownloadWorker worker = new DownloadWorker(getContext(), fileUrl, targetFileName, callback,
                new OnOperationCompleteListener() {
                    @Override
                    public void onOperationComplete(String fileUrl) {
                        mDownloadWorkers.remove(fileUrl);
                    }
                });

        mDownloadWorkers.put(fileUrl, worker);
        worker.execute();
    }

    public void downloadFileToPath(String fileUrl, String targetFolderPath, DownloaderCallback callback) {
        if (TextUtils.isEmpty(fileUrl)) {
            callback.onFailure(fileUrl, getContext().getString(R.string.error_file_name_is_empty));
            return;
        }

        if (TextUtils.isEmpty(targetFolderPath)) {
            callback.onFailure(fileUrl, getContext().getString(R.string.error_target_folder_path_is_empty));
            return;
        }

        if (mDownloadWorkers.containsKey(fileUrl.toLowerCase())) {
            callback.onFailure(fileUrl, getContext().getString(R.string.error_file_is_downloading, fileUrl));
            return;
        }

        if (!targetFolderPath.endsWith(File.separator)) {
            targetFolderPath = targetFolderPath + File.separator;
        }

        String targetFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        if (!targetFileName.contains(".")) {
            targetFileName = UUID.randomUUID().toString() + ".png";
        }

        targetFileName = targetFolderPath + targetFileName;

        download(fileUrl, targetFileName, callback);
    }

    public synchronized void cancel(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }

        DownloadWorker worker = mDownloadWorkers.get(fileUrl);
        if (worker != null) {
            worker.cancel();
        }
    }

    public synchronized void cancelAll() {
        for (Map.Entry<String, DownloadWorker> entry : mDownloadWorkers.entrySet()) {
            entry.getValue().cancel();
        }
    }

    public interface DownloaderCallback {
        void onSuccess(String fileUrl);

        void onFailure(String fileUrl, String errorMessage);

        void onCancel(String fileUrl);

        void onProgressUpdate(String fileUrl, long totalFileLength, long currentFileLength);
    }

    protected interface OnOperationCompleteListener {
        void onOperationComplete(String fileUrl);
    }

    protected enum DownloadStatus {
        SUCCESS,
        ERROR_SERVER_ERROR,
        ERROR_CREATE_TARGET_FILE_FAILED,
        ERROR_DOWNLOAD_FILE_FAILED,
        ERROR_REMOVE_EXISTING_FILE_FAILED,
        ERROR_RENAME_FILE_FAILED,
        CANCELLED
    }

    protected class DownloadWorker {
        public static final String TAG = "DownloadWorker";
        public static final String DOWNLOAD_FILE_NAME_SUFFIX = ".tmp";

        public String fileUrl;
        public String targetFileName;
        public AsyncTask<String, Void, DownloadStatus> task;
        public DownloaderCallback callback;

        protected OnOperationCompleteListener mOnOperationCompleteListener;
        protected boolean mIsCanceled;
        protected String mTempFileName;
        protected Context mContext;
        protected PowerManager.WakeLock mWakeLock;
        protected Handler mHandler;
        protected long mFileTotalLength;
        protected long mCurrentFileLength;
        protected int mIncreaseFileLength;

        public DownloadWorker(Context context, final String fileUrl, final String targetFileName, final DownloaderCallback callback,
                              OnOperationCompleteListener onOperationCompleteListener) {
            this.fileUrl = fileUrl;
            this.targetFileName = targetFileName;
            this.callback = callback;

            this.mContext = context;
            this.mHandler = new Handler(Looper.getMainLooper());
            this.mTempFileName = targetFileName + DOWNLOAD_FILE_NAME_SUFFIX;
            this.mOnOperationCompleteListener = onOperationCompleteListener;
            this.task = new AsyncTask<String, Void, DownloadStatus>() {

                @Override
                protected DownloadStatus doInBackground(String... params) {
                    String url = params[0];
                    return downloadFile(url, targetFileName, mTempFileName, callback);
                }

                @Override
                protected void onPreExecute() {
                    // take CPU lock to prevent CPU from going off if the user
                    // presses the power button during download
                    PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                            getClass().getName());
                    mWakeLock.acquire();
                }

                @Override
                protected void onPostExecute(DownloadStatus result) {
                    mWakeLock.release();

                    if (mOnOperationCompleteListener != null) {
                        mOnOperationCompleteListener.onOperationComplete(fileUrl);
                    }

                    switch (result) {
                        case SUCCESS:
                            callback.onSuccess(fileUrl);
                            return;
                        case CANCELLED:
                            callback.onCancel(fileUrl);
                            break;
                        default:
                            callback.onFailure(fileUrl, getContext().getString(R.string.error_download_file_failed, fileUrl));
                            break;
                    }
                }
            };
        }

        protected DownloadStatus downloadFile(final String fileUrl, String targetFileName,
                                              String tempFileName, final DownloaderCallback callback) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                if (!createFolderIfNotExist(targetFileName)) {
                    return DownloadStatus.ERROR_CREATE_TARGET_FILE_FAILED;
                }

                if (!createFolderIfNotExist(tempFileName)) {
                    return DownloadStatus.ERROR_CREATE_TARGET_FILE_FAILED;
                }

                if (!deleteFileIfExists(tempFileName)) {
                    return DownloadStatus.ERROR_REMOVE_EXISTING_FILE_FAILED;
                }

                URL url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Logger.d(TAG, String.format("Server return response code: %d", connection.getResponseCode()));
                    return DownloadStatus.ERROR_SERVER_ERROR;
                }

                mFileTotalLength = connection.getContentLength();

                input = connection.getInputStream();
                output = new FileOutputStream(tempFileName);

                byte data[] = new byte[4096];
                while ((mIncreaseFileLength = input.read(data)) != -1) {
                    if (mIsCanceled) {
                        input.close();
                        Logger.d(TAG, String.format("Cancel download file: %s", fileUrl));
                        return DownloadStatus.CANCELLED;
                    }

                    output.write(data, 0, mIncreaseFileLength);
                    mCurrentFileLength += mIncreaseFileLength;

                    if (mCurrentFileLength > 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onProgressUpdate(fileUrl, mFileTotalLength, mCurrentFileLength);
                            }
                        });
                    }
                }

                if (!renameFile(tempFileName, targetFileName)) {
                    return DownloadStatus.ERROR_RENAME_FILE_FAILED;
                }

                return DownloadStatus.SUCCESS;
            } catch (Exception e) {
                return DownloadStatus.ERROR_DOWNLOAD_FILE_FAILED;
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }

                    if (input != null) {
                        input.close();
                    }
                } catch (IOException ignored) {
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        public void execute() {
            Logger.d(TAG, String.format("Start to download file: %s to %s", fileUrl, targetFileName));
            task.execute(fileUrl);
        }

        public void cancel() {
            mIsCanceled = true;
        }

        protected synchronized boolean createFolderIfNotExist(String fileName) {
            if (TextUtils.isEmpty(fileName)) {
                return false;
            }

            File file = new File(fileName);
            if (file.isDirectory() && !file.exists()) {
                return file.mkdirs();
            }

            File path = file.getAbsoluteFile().getParentFile();
            if (path != null && !path.exists()) {
                return file.mkdirs();
            }

            return true;
        }

        protected synchronized boolean deleteFileIfExists(String fileName) {
            if (TextUtils.isEmpty(fileName)) {
                return false;
            }

            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                return file.delete();
            }

            return true;
        }

        protected boolean renameFile(String sourceFileName, String targetFileName) {
            File source = new File(sourceFileName);
            if (!source.exists()) {
                return false;
            }

            if (TextUtils.equals(sourceFileName, targetFileName)) {
                return true;
            }

            File target = new File(targetFileName);
            if (target.exists()) {
                if (!target.delete()) {
                    return false;
                }
            }

            return source.renameTo(target);
        }
    }
}