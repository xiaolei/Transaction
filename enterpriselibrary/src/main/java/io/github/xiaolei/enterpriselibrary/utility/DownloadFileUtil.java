package io.github.xiaolei.enterpriselibrary.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

import io.github.xiaolei.enterpriselibrary.listener.OnCancellableOperationCompletedListener;

/**
 * TODO: add comment
 */
public class DownloadFileUtil {
    public static void downloadFile(final Context context, final String fileUrl, String targetFileName,
                                    final OnCancellableOperationCompletedListener<String> onOperationCompletedListener) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(true);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DownloadManager.getInstance(context).cancel(fileUrl);
            }
        });

        DownloadManager.getInstance(context).download(fileUrl, targetFileName, new DownloadManager.DownloaderCallback() {
            @Override
            public void onSuccess(String fileUrl) {
                progressDialog.dismiss();
                onOperationCompletedListener.onOperationCompleted(false, true, fileUrl, null);
            }

            @Override
            public void onFailure(String fileUrl, String errorMessage) {
                progressDialog.dismiss();
                onOperationCompletedListener.onOperationCompleted(false, false, fileUrl, errorMessage);
            }

            @Override
            public void onCancel(String fileUrl) {
                progressDialog.dismiss();
                onOperationCompletedListener.onOperationCompleted(true, false, fileUrl, null);
            }

            @Override
            public void onProgressUpdate(String fileUrl, long totalFileLength, long currentFileLength) {
                int progress = (int) (currentFileLength * 100 / totalFileLength);

                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.setProgressNumberFormat(formatFileSize(currentFileLength) + " / " + formatFileSize(totalFileLength));
                progressDialog.setProgress(progress);
            }
        });

        progressDialog.show();
    }

    public static String getFileExtension(String fileUrl, String defaultExtension) {
        if (TextUtils.isEmpty(fileUrl)) {
            return defaultExtension;
        }

        int lastDotIndex = fileUrl.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileUrl.substring(lastDotIndex + 1);
        }

        return defaultExtension;
    }

    public static String getLocalFileUri(String localFileName){
        return Uri.fromFile(new File(localFileName)).toString();
    }

    public static String formatFileSize(long fileSize) {
        if (fileSize <= 0) {
            return "0M";
        }

        String result = "";
        if (fileSize < 1024) {
            // Less than 1K
            result = fileSize + "Bytes";
        } else if (fileSize < 1024 * 1024) {
            // Less than 1M
            result = String.format("%.1fK", fileSize / 1024.00f);
        } else {
            // Larger than 1M
            result = String.format("%.2fM", fileSize / (1024.00f * 1024.00f));
        }

        return result;
    }
}
