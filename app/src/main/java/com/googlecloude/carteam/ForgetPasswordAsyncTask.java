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

public class ForgetPasswordAsyncTask extends AsyncTask<String, Integer, String> {
    AlertDialog alertDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ForgetPasswordActivity.layoutInflater = (LayoutInflater) MyContext.getContext().getSystemService(ForgetPasswordActivity.LAYOUT_INFLATER_SERVICE);
        ForgetPasswordActivity.progressLayout = (LinearLayout) ForgetPasswordActivity.layoutInflater.inflate(R.layout.progresslayout, null);

        ForgetPasswordActivity.progressBar = ForgetPasswordActivity.progressLayout.findViewById(R.id.progressBar);
        ForgetPasswordActivity.progressBar.setProgress(0);
        ForgetPasswordActivity.progressBar.setMax(100);

        ForgetPasswordActivity.alertDialog = new AlertDialog.Builder(MyContext.getContext())
                            .setTitle("更改中....")
                            .setView(ForgetPasswordActivity.progressLayout)
                            .setCancelable(false);

        alertDialog = ForgetPasswordActivity.alertDialog.show();
    }

    @Override       //執行中
    protected String doInBackground(String... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(Url.forgetpassUrl + "?ac=" + ForgetPasswordActivity.account_et.getText().toString() + "&pa=" + ForgetPasswordActivity.pass_et.getText().toString());

            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(url.openStream()));

            String line = "";

            while((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);

            bufferedReader.close();
            publishProgress(100);

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        ForgetPasswordActivity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        alertDialog.dismiss();

        if(result != null){
            if(result.equals("saveok")){
                Toast.makeText(MyContext.getContext(), "密碼更改成功", Toast.LENGTH_SHORT).show();

                ForgetPasswordActivity.instance.finish();
            }
            else{
                ForgetPasswordActivity.result_tv.setText("找無此帳號，請重新輸入");
                ForgetPasswordActivity.account_et.setText("");
                ForgetPasswordActivity.pass_et.setText("");
                ForgetPasswordActivity.pass_c_et.setText("");
                ForgetPasswordActivity.account_et.setError(null);
                ForgetPasswordActivity.pass_et.setError(null);
                ForgetPasswordActivity.pass_c_et.setError(null);
            }
        }
        else{
            ForgetPasswordActivity.result_tv.setText("密碼更改失敗");
        }
    }
}
