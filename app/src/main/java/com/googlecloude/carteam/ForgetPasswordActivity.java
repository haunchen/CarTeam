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
import android.widget.TextView;

public class ForgetPasswordActivity extends AppCompatActivity {
    static ForgetPasswordActivity instance;
    static LayoutInflater layoutInflater;
    static LinearLayout progressLayout;
    static ProgressBar progressBar;
    static AlertDialog.Builder alertDialog;
    Button exit_btn, send_btn;
    static EditText account_et, pass_et, pass_c_et;
    static TextView result_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().hide();
        MyContext.setContext(this);
        instance = this;

        account_et = findViewById(R.id.account_et);
        pass_et = findViewById(R.id.pass_et);
        pass_c_et = findViewById(R.id.pass_c_et);
        result_tv = findViewById(R.id.result_tv);

        exit_btn = findViewById(R.id.exit_btn);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!account_et.getText().toString().equals("") && !pass_et.getText().toString().equals("") && !pass_c_et.getText().toString().equals("")){
                    if(pass_et.getText().toString().equals(pass_c_et.getText().toString())){
                        new ForgetPasswordAsyncTask().execute(Url.forgetpassUrl);
                    }
                    else{
                        pass_et.setError("兩個密碼不相同");
                        pass_c_et.setError("兩個密碼不相同");
                    }
                }
                else{
                    if(account_et.getText().toString().equals("")){
                        account_et.setError("請輸入帳號");
                    }
                    if(pass_et.getText().toString().equals("")){
                        pass_et.setError("請輸入新密碼");
                    }
                    if(pass_c_et.getText().toString().equals("")){
                        pass_c_et.setError("請輸入新密碼");
                    }
                }
            }
        });
    }
}
