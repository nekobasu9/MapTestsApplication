package jp.co.bizmobile.android.maptestsapplication;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,DataApi.DataListener{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    Vibrator vibrator;
    long[] pattern = {3000, 1000, 2000, 5000, 3000, 1000};

    TextView legsDistanceTextView = null;
    TextView legsDurationTextView = null;
    TextView stepsFirstDurationTextView = null;
    TextView stepsFirstDistanceTextView = null;

    TextView Html_instructionsListText;

    TextView stepsFirstDurationValueView;

    TextView requestTimeView;

    TextView countView;

    ImageView imageView;

    TextView stepsFirstDistanceValueView;


    GoogleApiClient mGoogleApiClient;
    String[] Html_instructionsList = null;
    String legsDistanceText = null;
    String legsDurationText = null;
    String stepsFirstDurationText = null;
    String stepsFirstDistanceText = null;
    int stepsFirstDistanceValue = 0;
    String manuever;

    int requestTime = 0;
    int stepsFirstDurationValue = 0;

    DicideManeuver dicideManeuver;

    int count = 0;
    private static final String TAG = "WearActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        //legsDistanceTextView = (TextView)findViewById(R.id.textView1);
        //legsDurationTextView = (TextView)findViewById(R.id.textView2);

        stepsFirstDistanceTextView = (TextView)findViewById(R.id.textView3);
        stepsFirstDurationTextView = (TextView)findViewById(R.id.textView4);

        Html_instructionsListText = (TextView)findViewById(R.id.textView5);


        stepsFirstDistanceValueView = (TextView)findViewById(R.id.textView);

        //countView = (TextView)findViewById(R.id.textView6);

        //stepsFirstDurationValueView = (TextView)findViewById(R.id.textView7);

        //requestTimeView = (TextView)findViewById(R.id.textView8);

        imageView = (ImageView)findViewById(R.id.imageView);



        dicideManeuver = new DicideManeuver();

        Log.d("drawable",""+R.drawable.droid);
        imageView.setImageResource(R.drawable.droid);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        mGoogleApiClient.connect();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));

            //legsDistanceTextView.setTextColor(getResources().getColor(android.R.color.white));
            //legsDurationTextView.setTextColor(getResources().getColor(android.R.color.white));

            stepsFirstDistanceTextView.setTextColor(getResources().getColor(android.R.color.white));
            stepsFirstDurationTextView.setTextColor(getResources().getColor(android.R.color.white));

            Html_instructionsListText.setTextColor(getResources().getColor(android.R.color.white));

            //countView.setTextColor(getResources().getColor(android.R.color.white));

            imageView.setColorFilter(0xccffffff, PorterDuff.Mode.SRC_IN);

            stepsFirstDistanceValueView.setTextColor(getResources().getColor(android.R.color.white));

            //stepsFirstDurationValueView.setTextColor(getResources().getColor(android.R.color.white));


            requestTimeView.setTextColor(getResources().getColor(android.R.color.white));

        } else {
            mContainerView.setBackground(null);

            //legsDistanceTextView.setTextColor(getResources().getColor(android.R.color.black));
            //legsDurationTextView.setTextColor(getResources().getColor(android.R.color.black));

            stepsFirstDistanceTextView.setTextColor(getResources().getColor(android.R.color.black));
            stepsFirstDurationTextView.setTextColor(getResources().getColor(android.R.color.black));

            Html_instructionsListText.setTextColor(getResources().getColor(android.R.color.black));

            imageView.setColorFilter(0xcc000000, PorterDuff.Mode.SRC_IN);

            stepsFirstDistanceValueView.setTextColor(getResources().getColor(android.R.color.black));

            //countView.setTextColor(getResources().getColor(android.R.color.black));
            //stepsFirstDurationValueView.setTextColor(getResources().getColor(android.R.color.black));
            //requestTimeView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    void setUpView(){


        for(int i= 0 ; i<Html_instructionsList.length - 1; i++) {
            Log.d("Html_instructionsList", Html_instructionsList[i]);

        }

        if(Html_instructionsList.length > 0){

            //legsDistanceTextView.setText("着:"+legsDistanceText);
            //legsDurationTextView.setText(":"+legsDurationText);

            stepsFirstDistanceTextView.setText("次:"+stepsFirstDistanceText);
            stepsFirstDurationTextView.setText("次:" + stepsFirstDurationText);

            stepsFirstDistanceValueView.setText("次:"+stepsFirstDistanceValue);

            String str ="";

            for(int i = 0;i<Html_instructionsList.length;i++){
                str += Html_instructionsList[i];

            }
            Html_instructionsListText.setText(str);

            int drawa = dicideManeuver.jadgeManeuver(manuever);

            Log.d("drawa", "" + drawa);
            imageView.setImageResource(drawa);

            //countView.setText("count" + count);

            //stepsFirstDurationValueView.setText("dv:"+stepsFirstDurationValue);

            //requestTimeView.setText("req:"+requestTime);

        }
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG,"onDataChanged");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                DataItem item = event.getDataItem();
                if(item.getUri().getPath().equals("/path"));{
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Html_instructionsList = dataMap.getStringArray("Html_instructionsList");

                    legsDistanceText = dataMap.getString("legsDistanceText");
                    legsDurationText = dataMap.getString("legsDurationText");
                    stepsFirstDurationText = dataMap.getString("stepsFirstDurationText");
                    stepsFirstDistanceText = dataMap.getString("stepsFirstDistanceText");
                    stepsFirstDistanceValue = dataMap.getInt("stepsFirstDistanceValue");
                    manuever = dataMap.getString("manuever");

                    requestTime = dataMap.getInt("requestTime");


                    stepsFirstDurationValue = dataMap.getInt("stepsFirstDurationValue");

                    Log.d("MainActivity",manuever);

                    count = dataMap.getInt("count");

                    Log.d("legsDistanceText",legsDistanceText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setUpView();
                            if (stepsFirstDistanceValue < 50){
                                //vibrator.vibrate(pattern, -1);

                            }
                        }
                    });

                }
            }
        }
    }

    // Activity stopで接続解除
    @Override
    protected void onDestroy() {

        super.onDestroy();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        Log.d(TAG, "onDestroy()");

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
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
