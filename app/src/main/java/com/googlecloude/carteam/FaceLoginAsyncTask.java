package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class FaceLoginAsyncTask extends AsyncTask<String, Integer, String> {
    AlertDialog alertDialog;
    String facetoken = "";
    String username = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        FaceLoginActivity.layoutInflater = (LayoutInflater) MyContext.getContext().getSystemService(MyContext.getContext().LAYOUT_INFLATER_SERVICE);
        FaceLoginActivity.progressLayout = (LinearLayout) FaceLoginActivity.layoutInflater.inflate(R.layout.progresslayout, null);

        FaceLoginActivity.progressBar = FaceLoginActivity.progressLayout.findViewById(R.id.progressBar);
        FaceLoginActivity.progressBar.setProgress(0);
        FaceLoginActivity.progressBar.setMax(100);

        FaceLoginActivity.alertDialog = new AlertDialog.Builder(MyContext.getContext())
                            .setTitle("登入中....")
                            .setView(FaceLoginActivity.progressLayout)
                            .setCancelable(false);

        alertDialog = FaceLoginActivity.alertDialog.show();
    }

    @Override       //執行中
    protected String doInBackground(String... urls) {
        InputStream inputStream;
        String responseResult;
        try {
            URL url_detect = new URL(Url.searchUrl);

            HttpsURLConnection conn_search = (HttpsURLConnection)url_detect.openConnection();
            search(conn_search);
            publishProgress(20);

            if(conn_search.getResponseCode() == 200) {
                inputStream = conn_search.getInputStream();
                responseResult = dealResponseResult(inputStream);
                Log.e("responseResult", responseResult);
                //String tmp = responseResult.substring(responseResult.indexOf("{"), responseResult.lastIndexOf("}") + 1);
                JSONObject jsonObject = new JSONObject(responseResult.substring(responseResult.indexOf("{"), responseResult.lastIndexOf("}") + 1));
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                float confidence = Float.parseFloat(jsonArray.getJSONObject(jsonArray.length() - 1).getString("confidence"));

                if(confidence > 80.0f){
                    facetoken = jsonArray.getJSONObject(jsonArray.length() - 1).getString("face_token");
                    FaceLoginActivity.result_tv.setText("找到人臉");
                    Log.e("facetoken", facetoken);
                }
                else{
                    FaceLoginActivity.result_tv.setText("找不到人臉");
                }
                publishProgress(30);

                StringBuilder stringBuilder = new StringBuilder();
                URL url = new URL(Url.faceloginUrl + "?face=" + facetoken);

                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(url.openStream()));

                String line = "";

                while((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line);

                bufferedReader.close();

                return stringBuilder.toString();
                //username = stringBuilder.toString();

                /*if(username.equals("Neno") || username.equals("")){
                    FaceLoginActivity.result_tv.setText("登入失敗");
                }
                else{

                }*/

                /*URL url_addface = new URL(Url.addfaceUrl);

                HttpsURLConnection conn_addface = (HttpsURLConnection)url_addface.openConnection();
                addFace(conn_addface);
                publishProgress(50);

                if(conn_addface.getResponseCode() == 200){
                    inputStream = conn_addface.getInputStream();
                    responseResult = dealResponseResult(inputStream);
                    Log.e("responseResult", responseResult);
                    JSONObject jsonObject_addface = new JSONObject(responseResult.substring(responseResult.indexOf("{"), responseResult.lastIndexOf("}") + 1));
                    publishProgress(80);
                    if(jsonObject_addface.getInt("face_added") == 1){
                        RegisterActivity.result_tv.setText("人臉新增成功");
                    }

                    responseResult = register();
                    Log.e("responseResult", responseResult);

                    publishProgress(100);
                    if(responseResult.equals("saveok")){
                        RegisterActivity.result_tv.setText("註冊成功");
                        //Toast.makeText(MyContext.getContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                        RegisterActivity.saveok = true;
                    }else if(responseResult.equals("error")){
                        RegisterActivity.result_tv.setText("註冊失敗，帳號已重複");
                        RegisterActivity.saveok = false;
                    }
                }else{
                    RegisterActivity.result_tv.setText("人臉新增失敗");
                }*/
            }
            else{
                FaceLoginActivity.result_tv.setText("找不到人臉");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FaceLoginActivity.result_tv.setText("註冊失敗，請檢查輸入的資料是否正確");
            //return "Unable to retrieve web page. URL may be invalid.";
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        FaceLoginActivity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        alertDialog.dismiss();

        if(result != null){
            if(result.equals("Neno")){
                Toast.makeText(MyContext.getContext(), "登入失敗", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MyContext.getContext(), "登入成功", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setClass(FaceLoginActivity.instance, MapsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("username", result);

                intent.putExtras(bundle);

                FaceLoginActivity.instance.startActivity(intent);
                FaceLoginActivity.instance.finish();
            }
        }
        else{
            Toast.makeText(MyContext.getContext(), "登入失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private void search(HttpsURLConnection conn){
        try {
            Map<String, String> detectMap = new HashMap<>();
            detectMap.put("api_key", Api.apiKey);
            detectMap.put("api_secret", Api.apiSecret);
            detectMap.put("image_base64", FaceLoginActivity.getImgBase64());
            detectMap.put("faceset_token", Api.faceset_token);
            String result = getRequestData(detectMap, "utf-8").toString();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    private void addFace(HttpsURLConnection conn){
        try{
            Map<String, String> addfaceMap = new HashMap<>();
            addfaceMap.put("api_key", Api.apiKey);
            addfaceMap.put("api_secret", Api.apiSecret);
            addfaceMap.put("faceset_token", Api.faceset_token);
            addfaceMap.put("face_tokens", facetoken);
            String result = getRequestData(addfaceMap, "utf-8").toString();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    private String register(){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(Url.registerUrl + "?ac=" + RegisterActivity.account_et.getText().toString() + "&pa=" + RegisterActivity.password_et.getText().toString() + "&fa=" + facetoken);

            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(url.openStream()));

            String line = "";

            while((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);

            /*Map<String, String> registerMap = new HashMap<>();
            registerMap.put("ac", RegisterActivity.account_et.getText().toString());
            registerMap.put("pa", RegisterActivity.password_et.getText().toString());
            String result = getRequestData(registerMap, "utf-8").toString();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            publishProgress(60);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(result.getBytes());
            publishProgress(70);*/

        } catch (IOException e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }
        return stringBuilder.toString();
    }

    private String dealResponseResult(InputStream inputStream) {
        String resultData = "";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    private StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();

        try{
            for(Map.Entry<String, String> entry : params.entrySet())
                stringBuffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode)).append("&");

            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }catch(Exception e){
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
