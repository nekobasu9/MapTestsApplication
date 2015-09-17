package jp.co.bizmobile.android.maptestsapplication;

/**
 * Created by shotaroyoshida on 2015/08/25.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by shotaroyoshida on 2015/08/21.
 */
public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient. OnConnectionFailedListener,LocationListener {

    static final String TAG="LocalService";

    private TextView textView = null;
    String text = null;
    SharedPreferences sharedPreferences;
    int requestTime;

    LatLng origin;
    LatLng dest;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    Handler handler = null;
    GetRoot getRoot = null;
    final int MAXREQUESTTIMES = 180000;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        sharedPreferences = getSharedPreferences("maps", Context.MODE_MULTI_PROCESS);

        getRoot = new GetRoot(getApplicationContext());



        handler = new Handler();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(MAXREQUESTTIMES);

        mGoogleApiClient.connect();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // 終了処理
//        mGoogleApiClient.disconnect();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);
        //Toast.makeText(this, "MyService#onStartCommand", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        handler = null;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        //Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
        //locationManager.removeUpdates(this);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i("TestService", "onBind");
        return null;
    }

    @Override
    public void onLocationChanged(Location location){

        Log.d("change", "change");

        if(location != null) {
            origin = new LatLng(location.getLatitude(), location.getLongitude());

            sharedPreferences = getSharedPreferences("maps",Context.MODE_MULTI_PROCESS);

            sharedPreferences.edit().putString("origin_latitude", String.valueOf(origin.latitude)).apply();
            sharedPreferences.edit().putString("origin_longitude", String.valueOf(origin.longitude)).apply();



            getRoot.root();
            setLocationTime();
        }

    }
//    @Override
//    public void onDisconnected() {
//
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
//    }


//    @Override
//    public void onProviderEnabled(String provider){
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras){
//
//        switch (status) {
//            case LocationProvider.AVAILABLE:
//                text += "LocationProvider.AVAILABLE\n";
//                //textView.setText(text);
//
//                break;
//            case LocationProvider.OUT_OF_SERVICE:
//                text += "LocationProvider.OUT_OF_SERVICE\n";
//                //textView.setText(text);
//                break;
//            case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                text += "LocationProvider.TEMPORARILY_UNAVAILABLE\n";
//                //textView.setText(text);
//                break;
//        }
//
//    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    void setLocationTime(){
        int stepsFirstDurationValue = sharedPreferences.getInt("stepsFirstDurationValue", 0);

        Log.d("stepsFirstDurationValu1",""+stepsFirstDurationValue);

        Log.d("requestTime0",""+stepsFirstDurationValue);

        Log.d("setlocation", "ServicesetLocationTime");

        requestTime = stepsFirstDurationValue / 3;

        Log.d("requestTime1",""+requestTime);

        if(requestTime<30){
            requestTime = 10;
        }

//        if (stepsFirstDurationValue <= 120){
//
//            requestTime = stepsFirstDurationValue;
//            Log.d("stepsFirstDurationValue","SERVICEsonomama"+stepsFirstDurationValue);
//
//        }else{
//
//            requestTime = stepsFirstDurationValue / 3;
//            Log.d("stepsFirstDrationValue","SERVICEhanbun"+stepsFirstDurationValue);
//        }

        Log.d("requestTime2",String.valueOf(requestTime));
        requestTime *=1000;
        Log.d("requestTime3",""+requestTime);
        //mLocationRequest.setInterval(requestTime);

        sharedPreferences.edit().putInt("requestTime", requestTime).apply();





        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                fusedLocationProviderApi.requestLocationUpdates(
//                        mGoogleApiClient, mLocationRequest, );
                getLocationUpdates();
            }
        }, requestTime);

//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest,this );


        //明示的にサービスの起動、停止が決められる場合の返り値

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, requestTime, Math.abs(requestTime-50), this);
    }

    void getLocationUpdates(){
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.d("onConnected", "onConnected");

        setLocationTime();
    }


    @Override
    public void onConnectionSuspended(int i) {
//        textLog += "onConnectionSuspended()\n";
//        textView.setText(textLog);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        textLog += "onConnectionFailed()\n";
//        textView.setText(textLog);
//
//        if (mResolvingError) {
//            // Already attempting to resolve an error.
//            Log.d("", "Already attempting to resolve an error");
//
//            return;
//        } else if (connectionResult.hasResolution()) {
//
//        } else {
//            mResolvingError = true;
//        }
    }

}
