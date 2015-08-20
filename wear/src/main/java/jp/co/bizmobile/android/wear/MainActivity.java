package jp.co.bizmobile.android.wear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements DataApi.DataListener{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    TextView legsDistanceTextView = null;
    TextView legsDurationTextView = null;
    TextView stepsFirstDurationTextView = null;
    TextView stepsFirstDistanceTextView = null;

    //ListView Html_instructionsListView = null;
    TextView Html_instructionsTextView = null;


    GoogleApiClient mGoogleApiClient;
    String[] Html_instructionsList = null;
    String legsDistanceText = null;
    String legsDurationText = null;
    String stepsFirstDurationText = null;
    String stepsFirstDistanceText = null;

    private static final String TAG = "WearActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        setUpView();

//        mTextView = (TextView) findViewById(R.id.text);
//        mClockView = (TextView) findViewById(R.id.clock);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
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
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));


            Html_instructionsTextView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));


            Html_instructionsTextView.setTextColor(getResources().getColor(android.R.color.black));


            mClockView.setVisibility(View.GONE);
        }
    }

    void setUpView(){


        legsDistanceTextView = (TextView)findViewById(R.id.textView1);
        legsDurationTextView = (TextView)findViewById(R.id.textView2);

        stepsFirstDistanceTextView = (TextView)findViewById(R.id.textView3);
        stepsFirstDurationTextView = (TextView)findViewById(R.id.textView4);

        //Html_instructionsListView = (ListView)findViewById(R.id.listView);
        Html_instructionsTextView = (TextView)findViewById(R.id.textView5);


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_expandable_list_item_1);
        if(Html_instructionsList.length > 0){
            //adapter.clear();
            //adapter.addAll(Html_instructionsList);

            legsDistanceTextView.setText("到着まで"+legsDistanceText);
            legsDurationTextView.setText("到着まで"+legsDurationText);

            stepsFirstDistanceTextView.setText("次"+stepsFirstDistanceText);
            stepsFirstDurationTextView.setText("次"+stepsFirstDurationText);
            String str = null;
            for(int i = 0;i<Html_instructionsList.length;i++){
                str += Html_instructionsList[i];
            }
            Html_instructionsTextView.setText(str);
        }
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
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


                    setUpView();
                }
            }
        }
    }
}
