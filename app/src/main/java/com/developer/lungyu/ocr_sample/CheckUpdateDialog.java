package com.developer.lungyu.ocr_sample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

public class CheckUpdateDialog extends Dialog implements View.OnClickListener {
    private Button btnOpenStorage,btnDetermine;
    private TextView tvStorageTitle,tvTitle,tvAlertText;
    private boolean determineStatus = false;
    private Activity activity;

    private Drawable blockadeDrawable,updateDrawable;

    public CheckUpdateDialog(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_check_update);
        setCanceledOnTouchOutside(false);
        setCancelable(false);//禁止返回鍵將dialog關閉

        init();//初始化
    }

    private void init(){
        tvStorageTitle = findViewById(R.id.tvStorageTitle);
        tvTitle = findViewById(R.id.tvTitle);
        tvAlertText = findViewById(R.id.tvAlertText);

        btnOpenStorage = findViewById(R.id.btnOpenStorage);
        btnDetermine = findViewById(R.id.btnDetermine);

        // Set the background of the dialog's root view to transparent, because Android puts your dialog layout within a root view that hides the corners in your custom layout.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnOpenStorage.setOnClickListener(this);
        btnDetermine.setOnClickListener(this);

        blockadeDrawable = activity.getResources().getDrawable(R.drawable.button_forbid);
        updateDrawable = activity.getResources().getDrawable(R.drawable.button_update);

        tvStorageTitle.setText("內存權限");
        checkStoragePermission();
    }

    public CheckUpdateDialog setText(String message){
        tvTitle.setText(message);
        return this;
    }

    public void checkStoragePermission(){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            btnOpenStorage.setText("請先開啟內存權限");
            btnDetermine.setBackground(blockadeDrawable);
            btnDetermine.setClickable(false);
        }else{
            btnOpenStorage.setText("已開啟內存權限");
            btnOpenStorage.setBackground(blockadeDrawable);
            btnOpenStorage.setClickable(false);
            btnDetermine.setBackground(updateDrawable);
            btnDetermine.setClickable(true);
        }
    }

    public boolean getStatus(){
        return determineStatus;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnOpenStorage:
                UtilTools.verifyStoragePermissions(activity);
                break;
            case R.id.btnDetermine:
                if(UtilTools.isNetworkAvailable(activity)){
                    determineStatus = true;
                    dismiss();
                }else{
                    tvAlertText.setText("請確認網路是否開啟");
                }
                break;
        }
    }
}
