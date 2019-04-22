package com.example.todoapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RemindReseiver extends BroadcastReceiver {

    Uri notif_uri ;
    Context context1 ;
    @Override
    public void onReceive(Context context, Intent intent) {

        notif_uri = intent.getData();
        String title = intent.getStringExtra("title");
        context1 = context ;

        PushNotification(title);

    }

    public void PushNotification( String title) {
        NotificationManager manager = (NotificationManager) context1.getSystemService(NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel description");
            channel.enableLights(true);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        int id = (int) ContentUris.parseId(notif_uri);

        Intent intentnotif = new Intent(context1, AddNote.class);
        intentnotif.setData(notif_uri);
        intentnotif.putExtra("ID", id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context1, NOTIFICATION_CHANNEL_ID)
                .setContentText("Do not forget to do this : " + title)
                .setContentTitle("Reminder !")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.iconfinder_empty);
        PendingIntent pendingNotification = PendingIntent.getActivity(context1, 0, intentnotif, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingNotification);
        manager.notify(id, builder.build());

        // startForeground(id,builder.build());
    }


}


