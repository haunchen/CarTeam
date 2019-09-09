package com.googlecloude.carteam;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {
    static boolean saveok = false;
    static RegisterActivity instance;
    static LayoutInflater layoutInflater;
    static LinearLayout progressLayout;
    static ProgressBar progressBar;
    static AlertDialog.Builder alertDialog;
    //static String facetoken;
    static Bitmap bitmap = null;

    private final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    Button cancel_btn, reset_btn, takeImg_btn, send_btn;
    ImageView showImg;
    static EditText account_et, password_et;
    static TextView result_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        MyContext.setContext(this);
        instance = this;

        showImg = findViewById(R.id.showImg);
        account_et = findViewById(R.id.account_et);
        password_et = findViewById(R.id.password_et);
        result_tv = findViewById(R.id.result_tv);

        takeImg_btn = findViewById(R.id.takeImg_btn);
        takeImg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Permission.hasPermission(PERMISSION_CAMERA)) {
                    if(Debug.ENABLE)
                        Toast.makeText(MyContext.getContext(), "調用相機", Toast.LENGTH_SHORT).show();
                    Camera.startCamera();
                }
                else{
                    Toast.makeText(MyContext.getContext(), "尚未取得權限", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reset_btn = findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.isImage = false;
                showImg.setImageResource(R.drawable.avatar);
                account_et.getText().clear();
                password_et.getText().clear();
            }
        });

        send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Camera.isImage){
                    //Toast.makeText(MyContext.getContext(), "有照片", Toast.LENGTH_SHORT).show();
                    if(!account_et.getText().toString().equals("") && !password_et.getText().toString().equals("")) {
                        new RegisterAsyncTask().execute(Url.detectUrl, Url.addfaceUrl);
                        //if(saveok) finish();
                    }
                    else{
                        if(account_et.getText().toString().equals(""))
                            account_et.setError("請輸入帳號");
                        if(password_et.getText().toString().equals(""))
                            password_et.setError("請輸入密碼");
                    }
                }else{
                    Toast.makeText(MyContext.getContext(), "請拍一張照片", Toast.LENGTH_SHORT).show();
                    if(account_et.getText().toString().equals(""))
                        account_et.setError("請輸入帳號");
                    if(password_et.getText().toString().equals(""))
                        password_et.setError("請輸入密碼");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            bitmap = Camera.showImage(showImg);
        }
    }

    public static String getImgBase64(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, bos);

        return new String(Base64.encode(bos.toByteArray(), Base64.DEFAULT));
    }
}
