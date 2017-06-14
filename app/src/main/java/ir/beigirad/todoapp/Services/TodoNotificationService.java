package ir.beigirad.todoapp.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ir.beigirad.todoapp.Activities.ReminderActivity;
import ir.beigirad.todoapp.R;

public class TodoNotificationService extends IntentService {
    public static final String TODOTEXT = "ir.beigirad.todonotificationservicetext";
    public static final String TODOUUID = "ir.beigirad.todonotificationserviceuuid";
    private String mTodoText;
    private String mTodoUUID;
    private Context mContext;

    public TodoNotificationService(){
        super("TodoNotificationService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoText = intent.getStringExtra(TODOTEXT);
        mTodoUUID =intent.getStringExtra(TODOUUID);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, ReminderActivity.class);
        i.putExtra(TodoNotificationService.TODOUUID, mTodoUUID);
        Intent deleteIntent = new Intent(this, DeleteNotificationService.class);
        deleteIntent.putExtra(TODOUUID, mTodoUUID);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDeleteIntent(PendingIntent.getService(this, mTodoUUID.hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(this, mTodoUUID.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        manager.notify(100, notification);
//        Uri defaultRingone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        MediaPlayer mp = new MediaPlayer();
//        try{
//            mp.setDataSource(this, defaultRingone);
//            mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
//            mp.prepare();
//            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    mp.release();
//                }
//            });
//            mp.start();
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
