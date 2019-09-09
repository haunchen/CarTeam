package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    static LoginActivity instance;
    static LayoutInflater layoutInflater;
    static LinearLayout progressLayout;
    static ProgressBar progressBar;
    static AlertDialog.Builder alertDialog;

    Button exit_btn, login_btn;
    static EditText account_et, pass_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        MyContext.setContext(this);
        instance = this;

        account_et = findViewById(R.id.account_et);
        pass_et = findViewById(R.id.pass_et);

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!account_et.getText().toString().equals("") && !pass_et.getText().toString().equals("")){
                    new LoginAsyncTask().execute(Url.loginUrl);
                    //Toast.makeText(LoginActivity.this, "登入", Toast.LENGTH_SHORT).show();
                }else{
                    if(account_et.getText().toString().equals("")){
                        account_et.setError("請輸入帳號");
                    }
                    if(pass_et.getText().toString().equals("")){
                        pass_et.setError("請輸入密碼");
                    }
                }
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
}
