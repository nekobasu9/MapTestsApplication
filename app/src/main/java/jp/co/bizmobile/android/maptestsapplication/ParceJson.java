package jp.co.bizmobile.android.maptestsapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shotaroyoshida on 2015/08/10.
 */
public class ParceJson extends Activity{

    private JSONArray routeArray = null;
    private JSONArray legsArray = null;
    private JSONObject legs = null;
    private JSONArray steps = null;
    private JSONObject stepsFirst = null;
    private JSONObject stepsSecond = null;

    private JSONObject stepsFirstDuration = null;
    private JSONObject stepsFirstDistance = null;
    private JSONObject legsFirst = null;
    private JSONObject legsSecond = null;
    private JSONObject routes = null;
    private JSONObject overviewPolylines = null;
    private JSONObject legsDuration = null;
    private JSONObject legsDistance = null;

    private String stepsFirstPolylinePoint = null;
    private String stepsSecondPolylinePoint = null;
    private String stepsSecondHtml_instructions = null;

    private String distance;
    private String[] Html_instructionsList;
    private Gson gson;

    SharedPreferences sharedPreferences = null;
    public void parce(Context context, JSONObject jsonObject){
        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            // sharedPreferences = getSharedPreferences("DataSave", Activity.MODE_PRIVATE);


            routeArray = jsonObject.getJSONArray("routes");
            routes = routeArray.getJSONObject(0);
            overviewPolylines = routes.getJSONObject("overview_polyline");
            legsArray = routes.getJSONArray("legs");
            legs = legsArray.getJSONObject(0);
            legsDuration = legs.getJSONObject("duration");
            legsDistance = legs.getJSONObject("distance");
            steps = legs.getJSONArray("steps");

            stepsFirst = steps.getJSONObject(0);

            stepsFirstDistance = stepsFirst.getJSONObject("distance");
            stepsFirstDuration = stepsFirst.getJSONObject("duration");
            stepsFirstPolylinePoint = stepsFirst.getJSONObject("polyline").getString("points");


            Log.d("polylinepoint", stepsFirstPolylinePoint);

            sharedPreferences.edit().putInt("stepsFirstDrationValue", stepsFirstDuration.getInt("value")).apply();
            sharedPreferences.edit().putString("stepsFirstDurationText", stepsFirstDuration.getString("text")).apply();

            sharedPreferences.edit().putInt("stepsFirstDistanceValue", stepsFirstDistance.getInt("value")).apply();
            sharedPreferences.edit().putString("stepsFirstDistanceText", stepsFirstDistance.getString("text")).apply();

            sharedPreferences.edit().putString("stepsFirstPolylinePoint", stepsFirstPolylinePoint).apply();

            sharedPreferences.edit().putString("overviewPolylines", overviewPolylines.getString("points")).apply();

            sharedPreferences.edit().putInt("legsDurationValue", legsDuration.getInt("value")).apply();
            sharedPreferences.edit().putString("legsDurationText", legsDuration.getString("text")).apply();
            sharedPreferences.edit().putInt("legsDistanceValue", legsDistance.getInt("value")).apply();
            sharedPreferences.edit().putString("legsDistanceText", legsDistance.getString("text")).apply();





            Log.d("null", String.valueOf(steps.isNull(1)));
            if (!(steps.isNull(1))) {
                stepsSecond = steps.getJSONObject(1);
            }else {
                stepsSecond = steps.getJSONObject(0);
            }

            stepsSecondPolylinePoint = stepsSecond.getJSONObject("polyline").getString("points");
            stepsSecondHtml_instructions = stepsSecond.getString("html_instructions");
            Log.d("html_instructions", stepsSecond.get("html_instructions").toString());

            sharedPreferences.edit().putString("stepsSecondPolylinePoint", stepsSecondPolylinePoint).apply();
            sharedPreferences.edit().putString("stepsSecondHtml_instructions", stepsSecondHtml_instructions).apply();
            Html_instructionsList = stepsSecondHtml_instructions.split("</b>",0);
            for(int i= 0 ; i<Html_instructionsList.length-1 ;i++ ){

                Log.d("Html_instructionsList",Html_instructionsList[i]);
                String str = Html_instructionsList[i];
                 //Log.d("str",""+str.length());
                //Log.d("strstr",str);
                int index = str.indexOf("<b>");
                Log.d("index",""+index);
                //String str01 = str.substring(1,4);
                String str01 = str.substring(index+3);
                Log.d("Html_instructionsList",str01);
                Html_instructionsList[i] = str01;



            }
            gson = new Gson();
            sharedPreferences.edit().putString("Html_instructionsList",gson.toJson(Html_instructionsList)).apply();






            Log.d("sharedPreferences",sharedPreferences.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("share","share");

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

}
