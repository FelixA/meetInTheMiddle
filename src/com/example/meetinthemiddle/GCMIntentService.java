package com.example.meetinthemiddle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import com.google.android.gcm.GCMBaseIntentService;
 
public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String PROJECT_ID = "355205271798";
     
    private static final String TAG = "GCMIntentService";
    
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;
    public GCMIntentService()
    {
        super(PROJECT_ID);
        Log.d(TAG, "GCMIntentService init");
    }
     
 
    @Override
    protected void onError(Context ctx, String sError) {
        // TODO Auto-generated method stub
        Log.d(TAG, "Error: " + sError);
         
    }
 
    @Override
    protected void onMessage(Context ctx, Intent intent) {
         
        Log.d(TAG, "Message Received");
         
        String message = intent.getStringExtra("message");
        sendNotification(message);
       
        sendGCMIntent(ctx, message);
         
    }
 
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DisplayLoginActivity.class), 0);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);      
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.meetinthemiddle)
        .setLargeIcon(bm)
        .setContentTitle("Meet In The Middle")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
    private void sendGCMIntent(Context ctx, String message) {
         
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("GCM_RECEIVED_ACTION");
         
        broadcastIntent.putExtra("gcm", message);
         
        ctx.sendBroadcast(broadcastIntent);
         
    }
     
     
    @Override
    protected void onRegistered(Context ctx, String regId) {
        // TODO Auto-generated method stub
        // send regId to your server
        Log.d(TAG, regId);
         
    }
 
    @Override
    protected void onUnregistered(Context ctx, String regId) {
        // TODO Auto-generated method stub
        // send notification to your server to remove that regId
         
    }
 
}