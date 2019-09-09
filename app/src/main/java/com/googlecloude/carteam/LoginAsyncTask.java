package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginAsyncTask extends AsyncTask<String, Integer, Void> {
    AlertDialog alertDialog;
    String login;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        LoginActivity.layoutInflater = (LayoutInflater) MyContext.getContext().getSystemService(LoginActivity.LAYOUT_INFLATER_SERVICE);
        LoginActivity.progressLayout = (LinearLayout) LoginActivity.layoutInflater.inflate(R.layout.progresslayout, null);

        LoginActivity.progressBar = LoginActivity.progressLayout.findViewById(R.id.progressBar);
        LoginActivity.progressBar.setProgress(0);
        LoginActivity.progressBar.setMax(100);

        LoginActivity.alertDialog = new AlertDialog.Builder(MyContext.getContext())
                            .setTitle("登入中....")
                            .setView(LoginActivity.progressLayout)
                            .setCancelable(false);

        alertDialog = LoginActivity.alertDialog.show();
    }

    @Override       //執行中
    protected Void doInBackground(String... urls) {
        try {
            login = login();
            publishProgress(100);

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
        LoginActivity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        alertDialog.dismiss();

        if(login.equals("success")){
            Toast.makeText(MyContext.getContext(), "登入成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(MyContext.getContext(), MapsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("username", LoginActivity.account_et.getText().toString());

            intent.putExtras(bundle);
            MyContext.getContext().startActivity(intent);
            LoginActivity.instance.finish();
        }
        else if(login.equals("Neno")){
            Toast.makeText(MyContext.getContext(), "登入失敗，請檢查帳號密碼是否輸入錯誤", Toast.LENGTH_SHORT).show();
        }
        try {

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String login(){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(Url.loginUrl + "?ac=" + LoginActivity.account_et.getText().toString() + "&pa=" + LoginActivity.pass_et.getText().toString());

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
