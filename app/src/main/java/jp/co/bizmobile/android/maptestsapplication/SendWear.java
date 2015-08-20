package jp.co.bizmobile.android.maptestsapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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

/**
 * Created by shotaroyoshida on 2015/08/19.
 */
public class SendWear extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,DataApi.DataListener{


    private GoogleApiClient mGoogleApiClient;
    SharedPreferences sharedPreferences;
    Gson gson;
    private String[] Html_instructionsList;
    String legsDistanceText;
    String legsDurationText;
    String stepsFirstDurationText;
    String stepsFirstDistanceText;

    private static final String TAG = "SendWear";

    void sendWear(){



        //これあとで試してみる
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mGoogleApiClient.connect();




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


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/path")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

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


}
