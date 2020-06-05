package com.developer.lungyu.ocr_sample;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class UtilTools {
    /**
     *  至另一個 activity 不留歷史紀錄
     **/
    public static void goActivity(Activity activity,Class destination){
        Intent intent = new Intent(activity.getApplicationContext(), destination);
        activity.startActivity(intent);
        activity.finish();//銷毀activity
    }

    public static void goActivity(Activity activity, Class destination, Bundle bundle){
        Intent intent = new Intent(activity.getApplicationContext(), destination);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();//銷毀activity
    }

    public static void goPreviousActivity(Activity activity, Class destination, Bundle bundle){
        Intent intent = new Intent(activity.getApplicationContext(), destination);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void goLogoutActivity(Activity activity, Class destination){
        Intent intent = new Intent(activity.getApplicationContext(), destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SharedPreferences.Editor editor  = activity.getSharedPreferences("session", Context.MODE_PRIVATE).edit();
        editor.clear();editor.commit();
        activity.startActivity(intent);
        activity.finish();//銷毀activity
    }

    /**
     *  傳送給 server  中文編碼
     **/
    public static String chineseEncoder(String inputStr){
        try {
            inputStr = URLEncoder.encode(inputStr,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return inputStr;
    }


    /* UTF-8 to BIG5 */
    public static String chineseDecode(String inputStr){
        String result = "";
        try {
            result = URLDecoder.decode(inputStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO 自動產生 catch 區塊
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  刪除資料夾內所有檔案
     * */
    public static void deleteAllFilesOfDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory() && fileOrDirectory.listFiles() != null){
            for (File child : fileOrDirectory.listFiles())
                deleteAllFilesOfDirectory(child);
        }else{
            fileOrDirectory.delete();
        }
    }

    /**
     *  確認手機網路
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *  確認手機內存權限
     */
    public static void verifyStoragePermissions(Activity activity) {
        // 內存權限
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    UtilCommonVariable.allowStoragePermission
            );
        }

    }

    /**
     *  確認手機相機權限
     */
    public static boolean verifyCameraPermissions(Activity activity,int requestCode) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA},requestCode);
            return false;
        }else{
            return true;
        }
    }

    public static String transChineseDate(String date){
        if(date.length() == 8){
            return (Integer.parseInt(date.substring(0,4))-1911) + "/" + date.substring(4,6)+ "/" + date.substring(6,8);
        } else {
            return date;
        }
    }

    public static String transChineseDateWithoutYear(String date){
        if(date.length() == 8){
            return date.substring(4,6)+ "/" + date.substring(6,8);
        } else {
            return date;
        }
    }

    public static String transTimeFormat(String time){
        if(time.length() == 4){
            return time.substring(0,2) + ":" + time.substring(2,4);
        }else if(time.length() == 6){
            return time.substring(0,2) + ":" + time.substring(2,4)+ ":" + time.substring(4,6);
        }else{
            return time;
        }
    }

    public static String transTimeFormatWithoutSecond(String time){
        if(time.length() == 4 || time.length() == 6){
            return time.substring(0,2) + ":" + time.substring(2,4);
        }else{
            return time;
        }
    }

    public static String getNowDate(Boolean transChinese){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        if(transChinese){
            return transChineseDate(simpleDateFormat.format(currentTime));
        }else{
            return simpleDateFormat.format(currentTime);
        }
    }

    public static String getNowTime6(Boolean transTimeFormat){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        if(transTimeFormat){
            return transTimeFormat(simpleDateFormat.format(currentTime));
        }else{
            return simpleDateFormat.format(currentTime);
        }
    }

    public static String getNowTime4(Boolean transTimeFormat){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        if(transTimeFormat){
            return transTimeFormat(simpleDateFormat.format(currentTime));
        }else{
            return simpleDateFormat.format(currentTime);
        }
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String zeroPadding(String original,int targetLength){
        if(original.length() == targetLength){
            return original;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(original);
        for(int i = original.length();i < targetLength;i++){
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    public static boolean isForegrounded(){
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public static String compress(String input) throws IOException {
        if (input == null || input.length() == 0) {
            return input;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOs = new GZIPOutputStream(out);
        gzipOs.write(input.getBytes());
        gzipOs.close();
        return out.toString("ISO-8859-1");
    }
    
    /**
     * @param zippedStr 壓縮後的字串
     * @return 解壓縮後的
     * @throws IOException IO
     */
    public static String uncompress(String zippedStr) throws IOException {
        if (zippedStr == null || zippedStr.length() == 0) {
            return zippedStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(zippedStr
                .getBytes("ISO-8859-1"));
        GZIPInputStream gzipIs = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gzipIs.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平臺預設編碼，也可以顯式的指定如toString("GBK")
        return out.toString();
    }

}
