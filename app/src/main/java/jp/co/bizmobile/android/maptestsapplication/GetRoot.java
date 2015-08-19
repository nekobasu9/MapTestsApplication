package jp.co.bizmobile.android.maptestsapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shotaroyoshida on 2015/08/19.
 */
public class GetRoot extends Activity{

    private RequestQueue mQueue;
    Gson gson;




    protected void root(LatLng origin,LatLng dest){
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


    private String getDirectionsUrl(LatLng origin,LatLng dest){


        String str_origin = "origin="+origin.latitude+","+origin.longitude;


        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=" + "driving";

        //JSON指定
        String output = "json";


        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }


}
