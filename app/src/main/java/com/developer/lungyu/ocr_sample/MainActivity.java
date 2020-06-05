package com.developer.lungyu.ocr_sample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    static final String TESSBASE_PATH = Environment.getExternalStorageDirectory() + "/chemo/";
    static final String TRAINEDDATA_LANGUAGE = "eng";
    public CheckUpdateDialog checkUpdateDialog;
    TessBaseAPI ocrApi;

    ImageView imgSrc;
    TextView txtResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        imgSrc = findViewById(R.id.imageView);
        txtResult = findViewById(R.id.textView1);
        ocrApi = new TessBaseAPI();

        checkUpdateDialog = new CheckUpdateDialog(this);
        checkUpdateDialog.create();

        checkPermission("");
    }

    private void checkPermission(String errorMsg){
        checkUpdateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(checkUpdateDialog.getStatus()){
                    getUnknownSourceAllowUpdateApp();
                }
            }
        });

        if(errorMsg.equals("rejectPermission")){
            checkUpdateDialog.setText("請開啟允許權限").show();
        }else{
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                checkUpdateDialog.setText("允許 APP 來源權限").show();
            }
            getUnknownSourceAllowUpdateApp();
        }
    }

    public void run(View v){
        checkTrainedDataExist();
        String ocrResult = ocrWithEnglish();
        txtResult.setText(ocrResult);
    }

    public String ocrWithEnglish() {
        String resString = "";

        imgSrc.setDrawingCacheEnabled(true);
        final Bitmap bitmap = imgSrc.getDrawingCache();

        ocrApi.init(TESSBASE_PATH, TRAINEDDATA_LANGUAGE);
        ocrApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        ocrApi.setImage(bitmap);
        resString = ocrApi.getUTF8Text();

        ocrApi.clear();
        ocrApi.end();
        return  resString;
    }

    private void copyTrainedDataFile() {
        String filename = "eng.traineddata";
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = TESSBASE_PATH + "tessdata/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void checkTrainedDataExist(){
        File folderPath = new File(TESSBASE_PATH);
        File tessdataFolderPath = new File(TESSBASE_PATH + "tessdata/");
        File tessdataFile = new File(tessdataFolderPath + File.separator + TRAINEDDATA_LANGUAGE +".traineddata");

        if (!folderPath.exists()){
            folderPath.mkdirs();
            tessdataFolderPath.mkdirs();
            copyTrainedDataFile();
        }else if (!tessdataFolderPath.exists() || !tessdataFolderPath.isDirectory()){
            tessdataFolderPath.mkdirs();
            copyTrainedDataFile();
        }else if (!tessdataFile.exists()){
            copyTrainedDataFile();
        }

    }

    public void getUnknownSourceAllowUpdateApp(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), UtilCommonVariable.allowUnknownPermission);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UtilCommonVariable.allowUnknownPermission && resultCode == Activity.RESULT_OK) {

        } else {
            checkPermission("rejectPermission");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == UtilCommonVariable.allowStoragePermission){
                checkUpdateDialog.checkStoragePermission();
            }

        }
    }
}
