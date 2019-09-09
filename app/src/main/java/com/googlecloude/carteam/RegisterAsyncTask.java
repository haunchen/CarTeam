package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RegisterAsyncTask extends AsyncTask<String, Integer, Void> {
    AlertDialog alertDialog;
    String facetoken = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        RegisterActivity.layoutInflater = (LayoutInflater) MyContext.getContext().getSystemService(MyContext.getContext().LAYOUT_INFLATER_SERVICE);
        RegisterActivity.progressLayout = (LinearLayout) RegisterActivity.layoutInflater.inflate(R.layout.progresslayout, null);

        RegisterActivity.progressBar = RegisterActivity.progressLayout.findViewById(R.id.progressBar);
        RegisterActivity.progressBar.setProgress(0);
        RegisterActivity.progressBar.setMax(100);

        RegisterActivity.alertDialog = new AlertDialog.Builder(MyContext.getContext())
                            .setTitle("註冊中....")
                            //.setMessage("上傳中......")
                            .setView(RegisterActivity.progressLayout)
                            .setCancelable(false);

        alertDialog = RegisterActivity.alertDialog.show();
    }

    @Override       //執行中
    protected Void doInBackground(String... urls) {
        InputStream inputStream;
        String responseResult;
        try {
            URL url_detect = new URL(Url.detectUrl);

            HttpsURLConnection conn_detect = (HttpsURLConnection)url_detect.openConnection();
            detect(conn_detect);
            publishProgress(20);

            if(conn_detect.getResponseCode() == 200) {
                inputStream = conn_detect.getInputStream();
                responseResult = dealResponseResult(inputStream);
                Log.e("responseResult", responseResult);
                //String tmp = responseResult.substring(responseResult.indexOf("{"), responseResult.lastIndexOf("}") + 1);
                JSONObject jsonObject = new JSONObject(responseResult.substring(responseResult.indexOf("{"), responseResult.lastIndexOf("}") + 1));
                JSONArray jsonArray = jsonObject.getJSONArray("faces");
                facetoken = jsonArray.getJSONObject(jsonArray.length() - 1).getString("face_token");
                publishProgress(30);

                if(facetoken != ""){
                    RegisterActivity.result_tv.setText("找到人臉");
                }else{
                    RegisterActivity.result_tv.setText("找不到人臉");
                }

                URL url_addface = new URL(Url.addfaceUrl);

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
                }
            }
            else{
                RegisterActivity.result_tv.setText("找不到人臉");
            }
        } catch (Exception e) {
            e.printStackTrace();
            RegisterActivity.result_tv.setText("註冊失敗，請檢查輸入的資料是否正確");
            //return "Unable to retrieve web page. URL may be invalid.";
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        RegisterActivity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        alertDialog.dismiss();
        if(RegisterActivity.saveok) {
            Toast.makeText(MyContext.getContext(), "註冊成功", Toast.LENGTH_SHORT).show();
            RegisterActivity.instance.finish();
        }
        else
            Toast.makeText(MyContext.getContext(), "註冊失敗", Toast.LENGTH_SHORT).show();
    }

    private void detect(HttpsURLConnection conn){
        try {
            Map<String, String> detectMap = new HashMap<>();
            detectMap.put("api_key", Api.apiKey);
            detectMap.put("api_secret", Api.apiSecret);
            detectMap.put("image_base64", RegisterActivity.getImgBase64());
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
