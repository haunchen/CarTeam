package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class InsertLocationAsyncTask extends AsyncTask<String, Integer, String> {
    AlertDialog alertDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override       //執行中
    protected String doInBackground(String... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(Url.insertlocationUrl + "?ac=" + MapsActivity.username + "&lan=" + MapsActivity.location.getLatitude() + "&lon=" + MapsActivity.location.getLongitude());

            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(url.openStream()));

            String line = "";

            while((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);

            bufferedReader.close();

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            //return "Unable to retrieve web page. URL may be invalid.";
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null){
            if(result.equals("saveok")){
                Toast.makeText(MapsActivity.instance, "新增位置成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MapsActivity.instance, "新增位置失敗", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(MapsActivity.instance, "新增位置失敗", Toast.LENGTH_SHORT).show();
        }
    }
}
