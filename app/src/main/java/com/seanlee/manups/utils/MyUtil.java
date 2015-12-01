package com.seanlee.manups.utils;

/**
 * Created by Sean Lee on 17/7/15.
 * Modified at 27/11/15
 */

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.seanlee.manups.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MyUtil {

    //-------------   General   -------------
    public static ProgressDialog getLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading_dialog));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        if (activity != null) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static boolean checkNetwork(Context context) {
        if (context != null) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } else {
            return false;
        }
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getAndroidID(Context context) {
        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    public static String genRStr() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateMD5(String string1, String string2) {
        String toDigest = string1 + string2;

        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(toDigest.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String generateMD5(String string) {
        return generateMD5(string, "");
    }

    //-------------   Time   -------------
    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

    /**
     * This function will convert normal Time format to mobiform time display format
     * @param timeString yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getDisplayTime(String timeString) {

        String displayTime = null;
        try {
            Date date = stringToDate(timeString, "yyyy-MM-dd HH:mm:ss");
            displayTime = dateToString(date,"HH:mma, dd MMM yyyy");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return displayTime;
    }

    /**
     * Transfer Date ==> String
     *
     * @param date
     * @param formatType format in yyyy-MM-dd HH:mm:ss or something like that
     * @return datetime in string
     */
    public static String dateToString(Date date, String formatType) {
        return new SimpleDateFormat(formatType, Locale.US).format(date);
    }

    /**
     * datetime long ==> String
     *
     * @param currentTime
     * @param formatType  The String format you want
     * @return
     * @throws ParseException
     */
    public static String longToString(long currentTime, String formatType) throws ParseException {
        Date date = longToDate(currentTime, formatType);
        String strTime = dateToString(date, formatType);
        return strTime;
    }

    /**
     * String ==> Date
     *
     * @param strTime
     * @param formatType the format should be same with strTime
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType, Locale.US);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static Date longToDate(long currentTime, String formatType) throws ParseException {
        Date dateOld = new Date(currentTime);
        String sDateTime = dateToString(dateOld, formatType);
        Date date = stringToDate(sDateTime, formatType);
        return date;
    }

    public static long stringToLong(String strTime, String formatType) throws ParseException {
        Date date = stringToDate(strTime, formatType);
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date);
            return currentTime;
        }
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    //-------------   Alert   -------------
    public static void myAlertDialog(Context mContext, String title, String content) {
        new Builder(mContext).setTitle(title).setMessage(content)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public static void myAlertDialogNoTitle(Context mContext, String content) {
        new Builder(mContext).setMessage(content).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast(int msgResource, Context context) {
        Toast.makeText(context, context.getString(msgResource), Toast.LENGTH_LONG).show();
    }

    //-------------   Image   -------------
    public static String convertBitmapToString(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap.hasAlpha())
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        else
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap convertStringToBitmap(String base64Data) {

        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static int getSampleSize(Context context, Uri uri) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenPoint = new Point();
        wm.getDefaultDisplay().getSize(screenPoint);

        Log.i("sean", "windowHeight:" + screenPoint.y + " windowWidth:" + screenPoint.x);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, opts);
        } catch (FileNotFoundException e) {
        }
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        Log.i("sean", "imageHeight:" + imageHeight + " imageWidth:" + imageWidth);

        int scaleX = imageWidth / screenPoint.x;
        int scaleY = imageHeight / screenPoint.y;
        Log.i("sean", "scaleX:" + scaleX + " scaleY:" + scaleY);

        int finalScale;
        if (scaleX > scaleY)
            finalScale = scaleX;
        else
            finalScale = scaleY;

        if (finalScale <= 1) {
            finalScale = 1;
            return finalScale;
        } else {
            return finalScale + 1;
        }
    }

    /**
     * This function just rotate that bitmap, it will not change the image
     *
     * @param context
     * @param uri     The bitmap uri
     * @param bitmap  The bitmap before rotate (Got from file or uri)
     * @return The rotated bitmap
     */
    public static Bitmap imageRotation(Context context, Uri uri, Bitmap bitmap) {

        Bitmap correctBmp = null;
        int angle = 0;
        boolean hasRotation = false;

        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            angle = cursor.getInt(0);
            hasRotation = true;
            cursor.close();
        }

        try {
            if (!hasRotation) {
                ExifInterface exif = new ExifInterface(uri.getPath());

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    angle = 90;
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    angle = 180;
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    angle = 270;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix mat = new Matrix();
        mat.postRotate(angle);

        correctBmp = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), mat, true);

        return correctBmp;
    }

}
