package jp.co.bizmobile.android.maptestsapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,DataApi.DataListener {

    private RequestQueue mQueue;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static Location mMyLocation = null;
    private static boolean mMyLocationCentering = false;
    ArrayList<LatLng> markerPoints;
    public static MarkerOptions options;
    Gson gson;
    LatLng origin;
    LatLng dest;
    SharedPreferences sharedPreferences = null;
    String stockStepsFirstPolylinePoint;
    int requestTime;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private String[] Html_instructionsList;
    String legsDistanceText;
    String legsDurationText;
    String stepsFirstDurationText;
    String stepsFirstDistanceText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mMap.setMyLocationEnabled(true);

        markerPoints = new ArrayList<LatLng>();

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("push", "pushbutton");
                origin = new LatLng(location.getLatitude(), location.getLongitude());

                sharedPreferences.edit().putString("origin_latitude", String.valueOf(origin.latitude)).apply();
                sharedPreferences.edit().putString("origin_longitude", String.valueOf(origin.longitude)).apply();

                Log.d("origin", "" + origin);
                mMyLocation = location;
                if (mMyLocation != null && mMyLocationCentering == false) { // 一度だけ現在地を画面中央に表示する
                    mMyLocationCentering = true;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()), 14.0f);
                    mMap.animateCamera(cameraUpdate);

// 逆ジオコーディングで現在地の住所を取得する
                    //requestReverseGeocode(location.getLatitude(), location.getLongitude());
                }
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.clear();
                dest = latLng;

                sharedPreferences.edit().putString("dest_latitude",String.valueOf(dest.latitude)).apply();
                sharedPreferences.edit().putString("dest_longitude",String.valueOf(dest.longitude)).apply();


                //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences();


                Log.d("LatLng", "" + latLng);
                markerPoints.add(latLng);

                options = new MarkerOptions();
                options.position(latLng);
                mMap.addMarker(options);

                Log.d("origin", "" + latLng.latitude);
                Log.d("dest", "" + latLng.longitude);
                root();
            }
        });

        // 停止ボタン
        Button btn2 = (Button)this.findViewById(R.id.button);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Pending Intent使ってレシーバーをセットする
//                PendingIntent sender = MapsActivity.this.getPending(MainActivity.this, 0);
                Intent intent = new Intent(MapsActivity.this,ReceiverAlert.class);
                PendingIntent sender = PendingIntent.getBroadcast(MapsActivity.this, 0, intent, 0);


                // アラームを解除する
                AlarmManager am = (AlarmManager) MapsActivity.this.getSystemService(ALARM_SERVICE);
                am.cancel(sender);

                Toast.makeText(MapsActivity.this, TAG + ": Alarmキャンセル！", Toast.LENGTH_SHORT).show();
                Log.d("cancel","cancel");
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        mGoogleApiClient.connect();
    }


    protected void root(){
        String url = getDirectionsUrl();

        mQueue = Volley.newRequestQueue(this);
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject json = new JSONObject(response);


                        Polyline line = null;


                        try {
                            //String status = response.getString("status");
                            //if(response.getString("status") == "OK") {
                            //    Log.d("status",response.getString("status"));


                            gson = new Gson();
                            String jsonInstanceString = gson.toJson(response);
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                            //SharedPreferences data = getSharedPreferences("directionDataSave", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString("directionData", jsonInstanceString).apply();

                            // Tranform the string into a json object
                            //final JSONObject json = new JSONObject(result);
                            //Log.d("response",response.toString(4));
                            ParceJson parceJson = new ParceJson();
                            parceJson.parce(getApplicationContext(), response);

                            //String stepsFirstDistance = parceJson.stepsFirstDistance.getString("test");

//                            JSONArray routeArray = response.getJSONArray("routes");
//                            JSONObject routes = routeArray.getJSONObject(0);
//                            JSONObject overviewPolylines = routes
//                                    .getJSONObject("overview_polyline");
//
                            String overviewPolylines = sharedPreferences.getString("overviewPolylines", null);
                            //String encodedString = overviewPolylines.getString("points");
                            List<LatLng> list = decodePoly(overviewPolylines);

                            for (int z = 0; z < list.size() - 1; z++) {
                                LatLng src = list.get(z);
                                LatLng dest = list.get(z + 1);
                                line = mMap.addPolyline(new PolylineOptions()
                                        .add(new LatLng(src.latitude, src.longitude),
                                                new LatLng(dest.latitude, dest.longitude))
                                        .width(5).color(Color.BLUE).geodesic(true));
                            }
//                            }else{
//                                Log.d("status",response.getString("status"));
//                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        sendWear();
                        startTimer();


//                        try {
//                            Log.d("response", response.toString(4));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", "" + error);
                    }
                }
        ));
        Log.d("test", "test");

    }


    void startTimer(){

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        int stepsFirstDrationValue = sharedPreferences.getInt("stepsFirstDrationValue", 0);

        if (stepsFirstDrationValue <= 120){

            requestTime = stepsFirstDrationValue;
            Log.d("stepsFirstDrationValue","sonomama"+stepsFirstDrationValue);

        }else{

            requestTime = stepsFirstDrationValue / 2;
            Log.d("stepsFirstDrationValue","hanbun"+stepsFirstDrationValue);
        }
        //requestTime *= 1000;
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                root();
//                Log.d("handler", "startroot");
//                //Toast.makeText(context, String.valueOf(++count), Toast.LENGTH_SHORT).show();
//            }
//        }, requestTime);


        Intent intent = new Intent(MapsActivity.this,ReceiverAlert.class);
        PendingIntent sender = PendingIntent.getBroadcast(MapsActivity.this,0,intent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, requestTime);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);

        Toast.makeText(MapsActivity.this, "Start Alarm!", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"alarmStart");


    }


    void sendWear(){
        Log.d("startSendWear","startSendWear");
        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String str = sharedPreferences.getString("Html_instructionsList",null);
        Html_instructionsList = gson.fromJson(str, String[].class);

        legsDistanceText = sharedPreferences.getString("legsDistanceText", null);
        legsDurationText = sharedPreferences.getString("legsDurationText", null);

        stepsFirstDurationText = sharedPreferences.getString("stepsFirstDistanceText",null);
        stepsFirstDistanceText = sharedPreferences.getString("stepsFirstDistanceText",null);



        for(int i= 0 ; i<Html_instructionsList.length - 1; i++) {
            Log.d("Html_instructionsList", Html_instructionsList[i]);

        }
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("handler", "sendWear");
                PutDataMapRequest mapReq = PutDataMapRequest.create("/path");
                mapReq.getDataMap().putStringArray("Html_instructionsList", Html_instructionsList);

                mapReq.getDataMap().putString("legsDistanceText", legsDistanceText);
                mapReq.getDataMap().putString("legsDurationText", legsDurationText);
                mapReq.getDataMap().putString("stepsFirstDurationText", stepsFirstDurationText);
                mapReq.getDataMap().putString("stepsFirstDistanceText", stepsFirstDistanceText);


                PutDataRequest request = mapReq.asPutDataRequest();
                if (!mGoogleApiClient.isConnected()) {
                    return;
                }
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                        .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(DataApi.DataItemResult dataItemResult) {
                                if (!dataItemResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "ERROR: failed to putDataItem, status code: "
                                            + dataItemResult.getStatus().getStatusCode());
                                }
                            }
                        });

            }
        });

    }


    void nannkamethod(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String stepsFirstPolylinePoint = sharedPreferences.getString("stepsFirstPolylinePoint", null);
        String stepsSecondPolylinePoint = sharedPreferences.getString("stepsSecondPolylinePoint",null);



        if(stepsFirstPolylinePoint == stepsSecondPolylinePoint){

        }
        if(stockStepsFirstPolylinePoint == stepsFirstPolylinePoint){

        }
        stockStepsFirstPolylinePoint = stepsFirstPolylinePoint;

    }



    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

/////////////////////
    private String getDirectionsUrl(){


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        String str_origin_latitude = sharedPreferences.getString("origin_latitude", null);
        String str_oringi_longitude = sharedPreferences.getString("origin_longitude", null);


        String str_origin = "origin="+str_origin_latitude+","+str_oringi_longitude;

//        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        //sharedPreferences.edit().putString("str_origin",str_origin).apply();


        String str_dest_latitude = sharedPreferences.getString("dest_latitude",null);
        String str_dest_longitude = sharedPreferences.getString("dest_longitude",null);


        String str_dest = "destination="+str_dest_latitude+","+str_dest_longitude;
        //String str_dest = "destination="+dest.latitude+","+dest.longitude;
        //sharedPreferences.edit().putString("str_dest",str_dest).apply();


        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=" + "driving";

        //JSON指定
        String output = "json";


        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    ////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    // Activity stopで接続解除
//    @Override
//    protected void onDestroy() {
//        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//        super.onDestroy();
//
//        Log.d(TAG, "onStop()");
//
//    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

//        LatLng sydney = new LatLng(-33.867, 151.206);
//
//        mMap.setMyLocationEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));//mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/testapp")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String name = dataMap.getString("name"); // "shokai"
                    String url  = dataMap.getString("url");  // "http://shokai.org"
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // 削除イベント
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.d("TAG", "onConnected");
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: " + connectionResult);
    }



    // Activity stopで接続解除
//    @Override
//    protected void onStop() {
//        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
//            Wearable.DataApi.removeListener(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
//        super.onStop();
//
//        Log.d(TAG, "onStop()");
//
//    }

    // Activity stopで接続解除
    @Override
    protected void onDestroy() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();

        Log.d(TAG, "onStop()");

    }

}
