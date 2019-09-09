package com.googlecloude.carteam;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {
    Button register_btn, login_btn, facelogin_btn, addface_btn, forgetpass_btn;

    private final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private final String PERMISSION_STORAGE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String PERMISSION_STORAGE_READ = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    private final String PERMISSION_GPS = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String PERMISSION_GPS_NETWOREK = Manifest.permission.ACCESS_COARSE_LOCATION;
    String[] perms = new String[]{PERMISSION_CAMERA, PERMISSION_STORAGE_WRITE, PERMISSION_STORAGE_READ, PERMISSION_INTERNET, PERMISSION_GPS, PERMISSION_GPS_NETWOREK};
    String[] perm_zh = new String[]{"相機", "寫入", "讀取", "網路", "GPS", "網路定位"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        MyContext.setContext(this);

        if(!Permission.hasPermission(perms))
            Permission.needCheckPermission(perms);

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyContext.getContext(), "註冊新用戶", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyContext.getContext(), "以帳號密碼登入", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        facelogin_btn = findViewById(R.id.facelogin_btn);
        facelogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyContext.getContext(), "以臉部辨識登入", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, FaceLoginActivity.class);
                startActivity(intent);
            }
        });

        addface_btn = findViewById(R.id.addface_btn);
        addface_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyContext.getContext(), "新增人臉", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, AddFaceActivity.class);
                startActivity(intent);
            }
        });

        forgetpass_btn = findViewById(R.id.forgetpass_btn);
        forgetpass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyContext.getContext(), "更改密碼", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 200){
            Permission.RequestResult(grantResults, perm_zh);
        }
    }
}
