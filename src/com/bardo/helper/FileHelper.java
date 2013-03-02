package com.bardo.helper;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class FileHelper {

    private FileHelper() {}

    public static Uri getNewImageFileUri() {
        File imageStorageDir = getImageFolder();
        if (imageStorageDir == null) return null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile = new File(imageStorageDir.getPath() + File.separator + "beerdog_" + timeStamp + ".jpg");
        return Uri.fromFile(imageFile);
    }

    public static Uri getImageUriFrom(String imageName) {
        if (!TextUtils.isEmpty(imageName)) {
            return Uri.fromFile(getImageFileFrom(imageName));
        } else {
            return null;
        }
    }

    public static File getImageFileFrom(String imageName) {
        return !TextUtils.isEmpty(imageName) ? new File(getImageFolderPath() + File.separator + imageName) : null;
    }

    public static File getImageFolder() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        // todo: sjekk om minnekort er der

        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "BeerDog");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        if (!imageStorageDir.exists()) {
            if (!imageStorageDir.mkdirs()) {
                Log.d("BeerDog", "failed to create directory");
                return null;
            }
        }
        return imageStorageDir;
    }

    private static String getImageFolderPath() {
        return getImageFolder().getPath();
    }
}
