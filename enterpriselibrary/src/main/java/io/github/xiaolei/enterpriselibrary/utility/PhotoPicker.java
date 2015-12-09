package io.github.xiaolei.enterpriselibrary.utility;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.xiaolei.enterpriselibrary.R;

/**
 * Photo picker. Provides photo related common methods.
 */
public class PhotoPicker {
    public static final int IMAGE_PICK = 1;
    public static final int IMAGE_CAPTURE = 2;
    public static final String PHOTO_FOLDER_NAME = "image";
    private static PhotoPicker INSTANCE = new PhotoPicker();
    private String mOutputPhotoFromCamera = "";

    private PhotoPicker() {
    }

    public synchronized static PhotoPicker getInstance() {
        return INSTANCE;
    }

    public String getCameraPhotoFileName() {
        return mOutputPhotoFromCamera;
    }

    public static String getPhotoStorageFolderName(Context context) {
        return context.getPackageName() + File.separator + PHOTO_FOLDER_NAME;
    }

    public synchronized void showPhotoPickerDialog(final Activity context) {
        if (context == null) {
            return;
        }

        AlertDialog photoPickerDialog = null;
        final String[] items = new String[]{context.getString(R.string.photo_picker_take_from_camera),
                context.getString(R.string.photo_picker_select_from_gallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.layout_item_dialog, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.photo_picker_select_image));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) { // Take photo from camera
                    takePhoto(context);
                } else {// pick from file
                    pickPhotoFromGallery(context);
                }
            }
        });

        photoPickerDialog = builder.create();
        photoPickerDialog.show();
    }

    public void takePhoto(Activity context) {
        Intent intent = new Intent(
                "android.media.action.IMAGE_CAPTURE");
        String photoStorageFolderPath = Environment
                .getExternalStorageDirectory() + File.separator + getPhotoStorageFolderName(context);
        File folder = new File(photoStorageFolderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        mOutputPhotoFromCamera = String.format(photoStorageFolderPath + File.separator + "IMG_%s.png", format.format(new Date()));
        File photo = new File(mOutputPhotoFromCamera);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        context.startActivityForResult(intent, IMAGE_CAPTURE);
    }

    public void pickPhotoFromGallery(Activity context) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        context.startActivityForResult(
                Intent.createChooser(intent, context.getString(R.string.photo_picker_choose_photo_from_gallery)),
                IMAGE_PICK);
    }

    public String extractImageUrlFromGallery(Context context, Intent data) {
        Uri selectedImage = data.getData();
        return selectedImage.toString();
    }

    public Bitmap extractImageFromGallery(Context context, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        String profile_Path = cursor.getString(columnIndex);
        cursor.close();

        Bitmap result = BitmapFactory.decodeFile(profile_Path);
        return result;
    }

    public Bitmap extractImageFromCamera(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        if (bitmap != null) {
            Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            return result;
        }

        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority())
                || "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
