package jp.co.bizmobile.android.maptestsapplication;

/**
 * Created by shotaroyoshida on 2015/08/19.
 */
import java.util.Calendar;

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
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ReceiverAlert extends BroadcastReceiver {

    private static final String TAG = "ReceiverAlert#";

    MapsActivity mapsActivity;
    // ______________________________________________________________________________
    @Override   // データを受信した
    public void onReceive(Context context, Intent intent) {

        mapsActivity = new MapsActivity();
        mapsActivity.root();
        // 時間の再設定が必要なもの
        if (intent.getAction() != null && (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || intent.getAction().equals(Intent.ACTION_DATE_CHANGED)
                || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)
                || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)
                || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED))) {

            // 表示
            Toast.makeText(context, TAG + ": アクションを受信 " + intent.getAction(), Toast.LENGTH_SHORT).show();

        } else {


            /*
            // スリープを解除する
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TAG");
            wl.acquire(10000);
            */
            String prefName = context.getPackageName() + "_preference";
            SharedPreferences sp = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);

            // 今のカウントを取り出す
            int count = sp.getInt("count", 0);

            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification n = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("時間です！")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(count + "回目")
                    .setContentText("お時間となりましたよ。")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();

            // 古い通知を削除
            nm.cancelAll();
            nm.notify(R.string.app_name, n);


            // カウントアップ
            Editor editor = sp.edit();
            editor.putInt("count", count + 1);
            editor.commit();




            Log.i(TAG + "onReceive", "時間です！" + count + " 回目");

            // 通常メッセージを受信
            Toast.makeText(context, TAG + " 時間です！" + count + " 回目", Toast.LENGTH_SHORT).show();

            //　しばらくは再通知
            if (count < 5) {
                // 再度タイマーをセットする
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(Calendar.SECOND, 10);

                AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), getPending(context, 0));
            } else {
                // 通常メッセージを受信
                Toast.makeText(context, TAG + ": タイマー終了！ ", Toast.LENGTH_SHORT).show();
            }

        }
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
