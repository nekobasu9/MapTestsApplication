package jp.co.bizmobile.android.maptestsapplication;

/**
 * Created by shotaroyoshida on 2015/08/25.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
//import com.google.android.gms.location.LocationListener;
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
public class GetRoot extends Activity //implements
        //LocationListener {
{
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

    private LocationManager locationManager;

    Context context;
    SendWear sendWear;

    GetRoot(Context appContext){

        context = appContext;
        sendWear = new SendWear(context);

    }





    protected void root(){



        sharedPreferences = context.getSharedPreferences("maps", Context.MODE_MULTI_PROCESS);
        Log.d("GetRoot","sharedPreferences");

        String url = getDirectionsUrl();

        Log.d("getRootURL",url);

        mQueue = Volley.newRequestQueue(context);
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            gson = new Gson();
                            String jsonInstanceString = gson.toJson(response);


                            //Log.d("responsJson", response.toString(4));

                            sharedPreferences.edit().putString("directionData", jsonInstanceString).apply();

                            ParceJson parceJson = new ParceJson();
                            parceJson.parce(context, response);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        sendWear.sendWear();

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


    private String getDirectionsUrl(){




        String str_origin_latitude = sharedPreferences.getString("origin_latitude", null);
        String str_oringi_longitude = sharedPreferences.getString("origin_longitude", null);


        String str_origin = "origin="+str_origin_latitude+","+str_oringi_longitude;


        String str_dest_latitude = sharedPreferences.getString("dest_latitude",null);
        String str_dest_longitude = sharedPreferences.getString("dest_longitude",null);


        String str_dest = "destination="+str_dest_latitude+","+str_dest_longitude;


        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=" + "driving";

        //JSON指定
        String output = "json";


        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
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



//    @Override
//    public void onLocationChanged(Location location){
//
//        if(location != null) {
//            origin = new LatLng(location.getLatitude(), location.getLongitude());
//            //String str = String.valueOf(origin.latitude);
//            //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//            sharedPreferences = context.getSharedPreferences("maps",Context.MODE_MULTI_PROCESS);
//
//            sharedPreferences.edit().putString("origin_latitude",String.valueOf(origin.latitude)).apply();
//            sharedPreferences.edit().putString("origin_longitude",String.valueOf(origin.longitude)).apply();
//
//            root();
//        }
//
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

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras){
//
//        switch (status) {
//            case LocationProvider.AVAILABLE:
////                text += "LocationProvider.AVAILABLE\n";
////                textView.setText(text);
//
//                break;
//            case LocationProvider.OUT_OF_SERVICE:
////                text += "LocationProvider.OUT_OF_SERVICE\n";
////                textView.setText(text);
//                break;
//            case LocationProvider.TEMPORARILY_UNAVAILABLE:
////                text += "LocationProvider.TEMPORARILY_UNAVAILABLE\n";
////                textView.setText(text);
//                break;
//        }
//
//    }
}
