package com.googlecloude.carteam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    static MapsActivity instance;
    TextView username_tv;
    Button getlocation_btn, logout_bt;
    static String username = "";
    static Location location = null;
    private static GoogleMap mGoogleMap;
    private final String PERMISSION_GPS = Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationManager lms;
    private boolean getService = false;     //是否已開啟定位服務
    private String bestProvider = LocationManager.GPS_PROVIDER;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 1000 * 10);
        }
        void update() {
            //刷新msg的内容
            mGoogleMap.clear();
            new GetLocationAsyncTask().execute(Url.getlocationUrl);

            new UpdateLocationAsyncTask().execute(Url.updatelocationUrl);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        instance = this;

        handler.postDelayed(runnable, 1000 * 10);
        new GetLocationAsyncTask().execute(Url.getlocationUrl);

        //mGoogleMap.clear();
        //new GetLocationAsyncTask().execute(Url.getlocationUrl);

        Bundle bundle = this.getIntent().getExtras();

        username = bundle.getString("username");

        username_tv = findViewById(R.id.username_tv);
        username_tv.setText("username : " + username);
        //取得定位系統服務
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
            new InsertLocationAsyncTask().execute(Url.insertlocationUrl);
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            getService = true; //確認開啟定位服務
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
        }

        getlocation_btn = findViewById(R.id.getlocation_btn);
        getlocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.clear();
                new GetLocationAsyncTask().execute(Url.getlocationUrl);
                new UpdateLocationAsyncTask().execute(Url.updatelocationUrl);
            }
        });

        logout_bt = findViewById(R.id.logout_bt);
        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteLocationAsyncTask().execute(Url.deletelocationUrl);
            }
        });
    }

    /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }

        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);

        LatLng latLng = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(15.0f)
                        .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    private void locationServiceInitial(){
        lms = (LocationManager)getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
            Location location = lms.getLastKnownLocation(bestProvider);
            getLocation(location);
            lms.requestLocationUpdates(bestProvider, 10, 0.01f, this);
        }
    }

    private void getLocation(Location location){
        if(location != null){
            this.location = location;
        }
        else{
            Toast.makeText(this, "無法定位", Toast.LENGTH_LONG).show();
        }
    }

    public static void Marker(String name ,Double Lan , Double Lon)
    {
        mGoogleMap.addMarker((new MarkerOptions().position(new LatLng(Lan,Lon)).title(name)));
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
        /*LatLng latLng = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(15.0f)
                        .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        //mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLongitude(), location.getLatitude())));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                if (getService) {
                    lms.requestLocationUpdates(bestProvider, 10, 0.01f, this);
                    //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
                }
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        new DeleteLocationAsyncTask().execute(Url.deletelocationUrl);
        super.onDestroy();
    }
}
