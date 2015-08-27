package jp.co.bizmobile.android.maptestsapplication;

/**
 * Created by shotaroyoshida on 2015/08/25.
 */
import android.app.Activity;
import android.content.Context;
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
public class SendWear implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,DataApi.DataListener{


    private GoogleApiClient mGoogleApiClient;
    SharedPreferences sharedPreferences;
    Gson gson;
    private String[] Html_instructionsList;
    String legsDistanceText;
    String legsDurationText;
    String stepsFirstDurationText;
    String stepsFirstDistanceText;
    String manuever;
    int count = 0;

    private static final String TAG = "SendWear";

    Context context;
    SendWear(Context appContext){

        context = appContext;

    }

    void sendWear() {


        //これあとで試してみる
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mGoogleApiClient.connect();

    }
    void startSendWear(){



        count++;
        Log.d("SendWearcount",""+count);

        Log.d("startSendWear","startSendWear");
        gson = new Gson();

        sharedPreferences = context.getSharedPreferences("maps", Context.MODE_MULTI_PROCESS);

        String str = sharedPreferences.getString("Html_instructionsList",null);
        Html_instructionsList = gson.fromJson(str, String[].class);

        legsDistanceText = sharedPreferences.getString("legsDistanceText", null);
        legsDurationText = sharedPreferences.getString("legsDurationText", null);

        stepsFirstDurationText = sharedPreferences.getString("stepsFirstDurationText",null);
        stepsFirstDistanceText = sharedPreferences.getString("stepsFirstDistanceText",null);

        manuever = sharedPreferences.getString("maneuver", null);

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
                mapReq.getDataMap().putString("manuever",manuever);

                mapReq.getDataMap().putInt("count",count);
                PutDataRequest request = mapReq.asPutDataRequest();
                if (!mGoogleApiClient.isConnected()) {
                    Log.d("sendwear","noConnected");
                    return;
                }
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                        .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(DataApi.DataItemResult dataItemResult) {
                                if (!dataItemResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "ERROR: failed to putDataItem, status code: "
                                            + dataItemResult.getStatus().getStatusCode());
                                } else {
                                    Log.d(TAG,"WearConnectSuccess");
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


                    //Log.d("Count",""+dataMap.getInt("count",0));

                    //Log.d("sendwear",dataMap.getString("legsDistanceText",null));

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // 削除イベント
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
        startSendWear();

    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended");
        Wearable.DataApi.removeListener(mGoogleApiClient,this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: " + connectionResult);
    }


}