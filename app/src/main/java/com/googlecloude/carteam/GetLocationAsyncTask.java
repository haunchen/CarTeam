package com.googlecloude.carteam;

import android.app.AlertDialog;
import android.os.AsyncTask;
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

public class GetLocationAsyncTask extends AsyncTask<String, Integer, String> {
    AlertDialog alertDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override       //執行中
    protected String doInBackground(String... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(Url.getlocationUrl + "?na=all");

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
            try {
                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String Name = obj.getString("Name");
                    if(!Name.equals(MapsActivity.username)) {
                        Double Lan = Double.valueOf(obj.getString("Lan"));
                        Double Lon = Double.valueOf(obj.getString("Lon"));
                        Log.e("JSON:", Name + " / " + Lan + " / " + Lon);
                        //Json data = new Json( Name, Lan, Lon);
                        //trans.add(data);
                        MapsActivity.Marker(Name, Lan, Lon);
                    }
                }
                //Toast.makeText(MapsActivity.instance, "獲取其他使用者位置成功", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(MapsActivity.instance, "找不到其他使用者", Toast.LENGTH_SHORT).show();
        }
    }
}
