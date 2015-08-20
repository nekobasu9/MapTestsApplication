package jp.co.bizmobile.android.maptestsapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by shotaroyoshida on 2015/08/19.
 */
public class GetRoot extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient. OnConnectionFailedListener,
        LocationListener {

    private RequestQueue mQueue;
    Gson gson;
    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private LocationRequest locationRequest;
    private Location location;

    private LatLng origin ;
    private LatLng dest;
    SharedPreferences sharedPreferences;
    int requestTime;

    void getLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        mGoogleApiClient.connect();

    }




    protected void root(){



        String url = getDirectionsUrl(origin,dest);

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
                            //String overviewPolylines = sharedPreferences.getString("overviewPolylines", null);
                            //String encodedString = overviewPolylines.getString("points");
                            //List<LatLng> list = decodePoly(overviewPolylines);

//                            for (int z = 0; z < list.size() - 1; z++) {
//                                LatLng src = list.get(z);
//                                LatLng dest = list.get(z + 1);
//                                line = mMap.addPolyline(new PolylineOptions()
//                                        .add(new LatLng(src.latitude, src.longitude),
//                                                new LatLng(dest.latitude, dest.longitude))
//                                        .width(5).color(Color.BLUE).geodesic(true));
//                            }
//                            }else{
//                                Log.d("status",response.getString("status"));
//                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //sendWear();
                        //startTimer();

                        new SendWear().sendWear();
                        startReceiver();


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


    private String getDirectionsUrl(LatLng origin,LatLng dest){


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


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


//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        for (DataEvent event : dataEvents) {
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                DataItem item = event.getDataItem();
//                if (item.getUri().getPath().equals("/testapp")) {
//                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    String name = dataMap.getString("name"); // "shokai"
//                    String url  = dataMap.getString("url");  // "http://shokai.org"
//                }
//            } else if (event.getType() == DataEvent.TYPE_DELETED) {
//                // 削除イベント
//            }
//        }
//    }


    @Override
    public void onLocationChanged(Location location){

    }
    @Override
    public void onConnected(Bundle bundle) {
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.d("TAG", "onConnected");
        Location currentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        if(currentLocation != null) {
            origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            //String str = String.valueOf(origin.latitude);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            sharedPreferences.edit().putString("origin_latitude",String.valueOf(origin.latitude)).apply();
            sharedPreferences.edit().putString("origin_longitude",String.valueOf(origin.longitude)).apply();

            root();
        }
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: " + connectionResult);
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


    void startReceiver(){

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



        Intent intent = new Intent(GetRoot.this,ReceiverAlert.class);
        PendingIntent sender = PendingIntent.getBroadcast(GetRoot.this,0,intent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, requestTime);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);

        //Toast.makeText(MapsActivity.this, "Start Alarm!", Toast.LENGTH_SHORT).show();
        Log.d("alarm","alarmStart");

//        Intent intent = new Intent(MapsActivity.this,ReceiverAlert.class);
//        PendingIntent sender = PendingIntent.getBroadcast(MapsActivity.this,0,intent,0);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, requestTime);
//
//        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);
//
//        Toast.makeText(MapsActivity.this, "Start Alarm!", Toast.LENGTH_SHORT).show();
//        Log.d(TAG,"alarmStart");


    }
}
