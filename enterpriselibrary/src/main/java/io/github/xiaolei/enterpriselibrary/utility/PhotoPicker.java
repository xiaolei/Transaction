package io.github.xiaolei.enterpriselibrary.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.xiaolei.enterpriselibrary.R;
import io.github.xiaolei.enterpriselibrary.logging.Logger;

/**
 * Photo picker. Provides photo related common methods.
 */
public class PhotoPicker {
    public static final String TAG = PhotoPicker.class.getSimpleName();
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
        if (!isExternalStorageWritable()) {
            DialogHelper.showAlertDialog(context, context.getString(R.string.msg_external_storage_not_writable));
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String photoStorageFolderPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + File.separator + getPhotoStorageFolderName(context);
        File folder = new File(photoStorageFolderPath);

        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                Logger.d(TAG, String.format("Failed to create dirs: %s", folder));
                DialogHelper.showAlertDialog(context, context.getString(R.string.error_failed_to_create_image_folder));
                return;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss", Locale.US);
        mOutputPhotoFromCamera = String.format(photoStorageFolderPath + File.separator + "IMG_%s.png", format.format(new Date()));
        File photo = new File(mOutputPhotoFromCamera);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            DialogHelper.showAlertDialog(context, context.getString(R.string.camera_app_not_found));
            return;
        }

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
        String result = selectedImage.toString();
        boolean isFilePath = result.startsWith(File.separator);
        if (isFilePath) {
            result = "file:///" + result;
        }

        return result;
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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
