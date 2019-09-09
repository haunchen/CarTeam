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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class FaceLoginActivity extends AppCompatActivity {
    static FaceLoginActivity instance;
    static LayoutInflater layoutInflater;
    static LinearLayout progressLayout;
    static ProgressBar progressBar;
    static AlertDialog.Builder alertDialog;
    static Bitmap bitmap;
    Button takepicture_btn, exit_btn;
    ImageView showImg;
    static TextView result_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_login);
        getSupportActionBar().hide();
        MyContext.setContext(this);
        instance = this;

        showImg = findViewById(R.id.showImg);
        result_tv = findViewById(R.id.result_tv);

        takepicture_btn = findViewById(R.id.takepicture_btn);
        takepicture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.startCamera();
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

            new FaceLoginAsyncTask().execute(Url.searchUrl);
        }
    }

    public static String getImgBase64(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, bos);

        return new String(Base64.encode(bos.toByteArray(), Base64.DEFAULT));
    }
}
