package com.googlecloude.carteam;

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

import java.io.ByteArrayOutputStream;

public class AddFaceActivity extends AppCompatActivity {
    static AddFaceActivity instance;
    static LayoutInflater layoutInflater;
    static LinearLayout progressLayout;
    static ProgressBar progressBar;
    static AlertDialog.Builder alertDialog;

    static String account = "";
    static Bitmap bitmap;
    static Button login_btn, takepicture_btn, addface_btn, exit_btn;
    static EditText account_et, password_et;
    static TextView login_tv, result_tv;
    ImageView showImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        getSupportActionBar().hide();
        MyContext.setContext(this);
        instance = this;

        account_et = findViewById(R.id.account_et);
        password_et = findViewById(R.id.password_et);
        login_tv = findViewById(R.id.login_tv);
        result_tv = findViewById(R.id.result_tv);
        showImg = findViewById(R.id.showImg);

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!account_et.getText().toString().equals("") && !password_et.getText().toString().equals("")){
                    new AddFaceLoginAsyncTask().execute(Url.loginUrl);
                }
                else{
                    if(account_et.getText().toString().equals("")){
                        account_et.setError("請輸入帳號");
                    }
                    if(password_et.getText().toString().equals("")){
                        password_et.setError("請輸入密碼");
                    }
                }
            }
        });

        takepicture_btn = findViewById(R.id.takepicture_btn);
        takepicture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.startCamera();
            }
        });

        addface_btn = findViewById(R.id.addface_btn);
        addface_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFaceAsyncTask().execute(Url.serverUrl);
            }
        });

        exit_btn = findViewById(R.id.exit_btn);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            bitmap = Camera.showImage(showImg);
            addface_btn.setEnabled(true);
        }
    }

    public static String getImgBase64(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, bos);

        return new String(Base64.encode(bos.toByteArray(), Base64.DEFAULT));
    }
}
