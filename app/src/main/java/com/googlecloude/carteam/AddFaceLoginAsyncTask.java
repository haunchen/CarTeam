package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class AddFaceLoginAsyncTask extends AsyncTask<String, Integer, Void> {
    AlertDialog alertDialog;
    String login;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AddFaceActivity.layoutInflater = (LayoutInflater) MyContext.getContext().getSystemService(AddFaceActivity.LAYOUT_INFLATER_SERVICE);
        AddFaceActivity.progressLayout = (LinearLayout) AddFaceActivity.layoutInflater.inflate(R.layout.progresslayout, null);

        AddFaceActivity.progressBar = AddFaceActivity.progressLayout.findViewById(R.id.progressBar);
        AddFaceActivity.progressBar.setProgress(0);
        AddFaceActivity.progressBar.setMax(100);

        AddFaceActivity.alertDialog = new AlertDialog.Builder(MyContext.getContext())
                            .setTitle("登入中....")
                            .setView(AddFaceActivity.progressLayout)
                            .setCancelable(false);

        alertDialog = AddFaceActivity.alertDialog.show();
    }

    @Override       //執行中
    protected Void doInBackground(String... urls) {
        try {
            login = login();

            Log.e("login", login);
        } catch (Exception e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        AddFaceActivity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        alertDialog.dismiss();

        if(login.equals("success")){
            Toast.makeText(MyContext.getContext(), "登入成功", Toast.LENGTH_SHORT).show();
            AddFaceActivity.login_tv.setText("登入成功");
            AddFaceActivity.takepicture_btn.setEnabled(true);
            AddFaceActivity.account = AddFaceActivity.account_et.getText().toString();

            /*Intent intent = new Intent();
            intent.setClass(MyContext.getContext(), MapsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("username", AddFaceActivity.account_et.getText().toString());

            intent.putExtras(bundle);
            MyContext.getContext().startActivity(intent);
            AddFaceActivity.instance.finish();*/
        }
        else if(login.equals("Neno")){
            Toast.makeText(MyContext.getContext(), "登入失敗，請檢查帳號密碼是否輸入錯誤", Toast.LENGTH_SHORT).show();
            AddFaceActivity.login_tv.setText("登入失敗");
        }
    }

    private String login(){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(Url.loginUrl + "?ac=" + AddFaceActivity.account_et.getText().toString() + "&pa=" + AddFaceActivity.password_et.getText().toString());

            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(url.openStream()));

            String line = "";

            while((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }
        return stringBuilder.toString();
    }
}
