package com.newproject.tamagotchi_foad;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    /**
     * Shared preferences
     */
    public static final String SHARED_PREFERENCES = "sharedPreferences";// General app code
    public static final String LAST_TIME_ACTIVE = "lastTimeActive";// Time when user last exited the app
    public static final String NOTIFICATION_ID = "notificationID";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * Timers
     */
    Timer healthTimer, happinessTimer, affectionTimer, saturationTimer;

    /**
     * Notification variable declaration
     */
    private NotificationManagerCompat notificationManagerCompat;
    private int notificationId = 0;
    private String notificationChannelId;

    MediaPlayer mediaPlayer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Something title")
                    .setContentText("Something text").build();

            startForeground(1, notification);
        }

        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        /**
         * Shared preference initializing
         */
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /**
         * Notification data
         */
        notificationChannelId = "channel1";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel(notificationChannelId, notificationChannelId, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        notificationManagerCompat = NotificationManagerCompat.from(this);

        healthTimer = new Timer();
        happinessTimer = new Timer();
        affectionTimer = new Timer();
        saturationTimer = new Timer();

        int healthDecrement = intent.getIntExtra("Health decrement", -1);
        int happinessDecrement = intent.getIntExtra("Happiness decrement", -1);
        int affectionDecrement = intent.getIntExtra("Affection decrement", -1);
        int saturationDecrement = intent.getIntExtra("Saturation decrement", -1);

        int health = intent.getIntExtra("Health", 0);
        int happiness = intent.getIntExtra("Happiness", 0);
        int affection = intent.getIntExtra("Affection", 0);
        int saturation = intent.getIntExtra("Saturation", 0);

        if(healthDecrement * health * 0.94 > 0) {
            healthTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SendNotification("", "");
                }
            }, (int)(healthDecrement * health * 0.94));
        }
        if(happinessDecrement * happiness * 0.94 > 0) {
            happinessTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SendNotification("", "");
                }
            }, (int)(happinessDecrement * happiness * 0.94));
        }
        if(affectionDecrement * affection * 0.94 > 0) {
            affectionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SendNotification("", "");
                }
            }, (int)(affectionDecrement * affection * 0.94));
        }
        if(saturationDecrement * saturation * 0.94 > 0) {
            saturationTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SendNotification("", "");
                }
            }, (int)(saturationDecrement * saturation * 0.94));
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        healthTimer.cancel();
        happinessTimer.cancel();
        saturationTimer.cancel();
        affectionTimer.cancel();
        mediaPlayer.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void SendNotification(CharSequence title, CharSequence text) {
        //Seems to be done and working
        Intent intent = new Intent(this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.drawable.baseline_stars_black_48dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setAutoCancel(true);

        notificationManagerCompat.notify(notificationId, builder.build());
        notificationId++;
    }
}
