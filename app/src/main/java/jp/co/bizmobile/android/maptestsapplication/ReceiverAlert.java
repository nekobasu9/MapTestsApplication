package jp.co.bizmobile.android.maptestsapplication;

/**
 * Created by shotaroyoshida on 2015/08/19.
 */
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

public class ReceiverAlert extends BroadcastReceiver {

    private static final String TAG = "ReceiverAlert#";

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
    private String[] Html_instructionsList;
    String legsDistanceText;
    String legsDurationText;
    String stepsFirstDurationText;
    String stepsFirstDistanceText;

    GetRoot getRoot;


    // ______________________________________________________________________________
    @Override   // データを受信した
    public void onReceive(Context context, Intent intent) {

        getRoot = new GetRoot();
        getRoot.getLocation();


            /*
            // スリープを解除する
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TAG");
            wl.acquire(10000);
            */

//            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            Notification n = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setTicker("ルート案内中です")
//                    .setWhen(System.currentTimeMillis())
//                    .setContentTitle("ルート案内中です")
//                    .setContentText("ルート案内中です。")
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .build();




//
//            Log.i(TAG + "onReceive", "時間です！" + count + " 回目");

//
//            //　しばらくは再通知
//            if (count < 5) {
//                // 再度タイマーをセットする
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(System.currentTimeMillis());
//                cal.add(Calendar.SECOND, 10);
//
//                AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), getPending(context, 0));
//            } else {
//                // 通常メッセージを受信
//                Toast.makeText(context, TAG + ": タイマー終了！ ", Toast.LENGTH_SHORT).show();
//            }


    }

    // ______________________________________________________________________________
    /**
     * メンバ変数として保存ではなくて、そのつど作らないとキャンセルが効かない場合があるみたい
     * @param ctx コンテキスト
     * @param uid ユニークなID
     * @return PendingIntent
     */
    private PendingIntent getPending(Context ctx, int uid) {
        Intent indent = new Intent(ctx, ReceiverAlert.class);
        PendingIntent pending = PendingIntent.getBroadcast(ctx, uid, indent, 0);

        return pending;
    }

}
