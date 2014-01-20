package com.example.meetinthemiddle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.NotificationCompat.Builder;
import com.google.android.gcm.GCMBaseIntentService;
 
public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String PROJECT_ID = "355205271798";
     
    private static final String TAG = "GCMIntentService";
     
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
         
        sendGCMIntent(ctx, message);
         
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