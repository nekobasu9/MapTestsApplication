package jp.co.bizmobile.android.maptestsapplication;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by shotaroyoshida on 2015/08/21.
 */
public class MyService extends Service implements LocationListener {

    static final String TAG="LocalService";

    private LocationManager locationManager;
    private TextView textView = null;
    String text = null;
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        Toast.makeText(this, "MyService#onCreate", Toast.LENGTH_SHORT).show();

        //textView = (TextView)findViewById(R.id.textView);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);



        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);
        Toast.makeText(this, "MyService#onStartCommand", Toast.LENGTH_SHORT).show();
        //明示的にサービスの起動、停止が決められる場合の返り値

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i("TestService", "onBind");
        return null;
    }

    @Override
    public void onLocationChanged(Location location){

        Log.d("change","change");
        text += "----------\n";
        text += "Latitude="+ String.valueOf(location.getLatitude())+"\n";
        text += "Longitude="+ String.valueOf(location.getLongitude())+"\n";
        //textView.setText(text);

        Log.d("service",text);

    }


    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

        switch (status) {
            case LocationProvider.AVAILABLE:
                text += "LocationProvider.AVAILABLE\n";
                //textView.setText(text);

                break;
            case LocationProvider.OUT_OF_SERVICE:
                text += "LocationProvider.OUT_OF_SERVICE\n";
                //textView.setText(text);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                text += "LocationProvider.TEMPORARILY_UNAVAILABLE\n";
                //textView.setText(text);
                break;
        }

    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

}

