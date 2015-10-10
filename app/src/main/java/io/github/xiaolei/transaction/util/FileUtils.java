package io.github.xiaolei.transaction.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * TODO: add comment
 */
public class FileUtils {
    public static void copyToFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static boolean createFolderPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String targetPath = path;
        if (!path.startsWith(File.separator)) {
            targetPath = File.separator + path;
        }

        File folder = new File(targetPath);
        if (!folder.exists()) {
            return folder.mkdirs();
        } else {
            return true;
        }
    }

    public static boolean createFolderPathInExternalStorage(String path) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }

        return createFolderPath(path);
    }

    public static boolean createFolderPathInPicturesFolder(String folderName) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }

        String targetPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath().toString();
        if (!folderName.startsWith(File.separator)) {
            targetPath = targetPath + File.separator + folderName;
        }

        return createFolderPath(targetPath);
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
