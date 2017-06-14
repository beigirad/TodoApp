package ir.beigirad.todoapp.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by farhad-mbp on 6/14/17.
 */

public class AlarmUtil {
    Context mContext;

    public AlarmUtil(Context mContext) {
        this.mContext = mContext;
    }


    private boolean doesPendingIntentExist(Intent i, int requestCode) {
        PendingIntent pi = PendingIntent.getService(mContext, requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(mContext, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
    }

    public void deleteAlarm(Intent i, int requestCode) {
        if (doesPendingIntentExist(i, requestCode)) {
            PendingIntent pi = PendingIntent.getService(mContext, requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            ((AlarmManager) mContext.getSystemService(ALARM_SERVICE)).cancel(pi);
        }
    }
}
