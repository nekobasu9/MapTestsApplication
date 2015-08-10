package jp.co.bizmobile.android.maptestsapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shotaroyoshida on 2015/08/10.
 */
public class ParceJson extends Activity{

    JSONArray routeArray = null;
    JSONArray legsArray = null;
    JSONObject legs = null;
    JSONArray steps = null;
    JSONObject stepsFirst = null;
    JSONObject stepsSecond = null;

    JSONObject stepsFirstDration = null;
    JSONObject stepsFirstDistance = null;
    JSONObject legsFirst = null;
    JSONObject legsSecond = null;
    JSONObject routes = null;
    JSONObject overviewPolylines = null;
    String stepsFirstPolylinePoint = null;
    String stepsSecondPolylinePoint = null;
    String stepsSecondHtml_instructions = null;

    String distance;

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
            steps = legs.getJSONArray("steps");

            stepsFirst = steps.getJSONObject(0);

            stepsFirstDistance = stepsFirst.getJSONObject("distance");
            stepsFirstDration = stepsFirst.getJSONObject("duration");
            stepsFirstPolylinePoint = stepsFirst.getJSONObject("polyline").getString("points");


            Log.d("polylinepoint", stepsFirstPolylinePoint);

            sharedPreferences.edit().putString("stepsFirstDistance", stepsFirstDistance.getString("text")).apply();
            sharedPreferences.edit().putString("stepsFirstDration",stepsFirstDration.getString("text")).apply();
            sharedPreferences.edit().putString("stepsFirstPolylinePoint", stepsFirstPolylinePoint).apply();
            sharedPreferences.edit().putString("overviewPolylines",overviewPolylines.getString("points")).apply();


            Log.d("null", String.valueOf(steps.isNull(1)));
            if (!(steps.isNull(1))){
                stepsSecond = steps.getJSONObject(1);
                stepsSecondPolylinePoint = stepsSecond.getJSONObject("polyline").getString("points");
                stepsSecondHtml_instructions = stepsSecond.getString("html_instructions");
                Log.d("html_instructions",stepsSecond.get("html_instructions").toString());

                sharedPreferences.edit().putString("stepsSecondPolylinePoint",stepsSecondPolylinePoint).apply();
                sharedPreferences.edit().putString("stepsSecondHtml_instructions",stepsSecondHtml_instructions).apply();
            }

            Log.d("sharedPreferences",sharedPreferences.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("share","share");

    }


}
