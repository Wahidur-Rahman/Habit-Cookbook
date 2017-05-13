package com.example.wahidur.fragments;
/**
 * Part of the Alarm Receiver snippet and works in conjunction with it. Similarly adapted form the demo on
 * https://developer.android.com/training/scheduling/alarms.html
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.wahidur.habit_cookbook.ConfirmedNotification;
import com.example.wahidur.habit_cookbook.R;
import com.example.wahidur.routine_structure.Routine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class AlarmReceiver extends BroadcastReceiver {
    public String name = "";
    int id = 0;
    String type = "PRE";
    long time = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        //
        Routine alarmCollections = new Routine();
        alarmCollections.attemptListCreation(context, "RoutineList.json");
        JSONArray routineArray = alarmCollections.getRoutineArray();
        JSONArray tempArray = new JSONArray();
        for(int i = 0; i <routineArray.length();i++){
            try {
                tempArray.put(routineArray.get(i));
            }catch (Exception e){

            }
        }

        //
        try{
            Bundle extras = intent.getExtras();
            int id = extras.getInt("ID");
            Log.w("ID",Integer.toString(id));
            Log.w("GOT HERE","GOT HERE8");
            name = extras.getString("NAME");
            type = extras.getString("TYPE");
            time = extras.getLong("TIME");

        } catch (Exception e){

        }
        if(type.equals("PRE")){
            createEmptyNotification(context, name + " soon!", "Don't forget to do your habits", name + " soon");
            try {
                //Attempt to store time from alarm.
                for (int i = 0; i < routineArray.length(); i++) {
                    JSONObject temp = (JSONObject) routineArray.get(i);
                    if (temp.has(name)) {
                        JSONArray substitute = (JSONArray) temp.get(name);
                        long postTemp = substitute.getLong(6);
                        substitute.remove(6);
                        substitute.remove(5);
                        substitute.put(5, time);
                        substitute.put(6, postTemp);
                        temp.remove(name);
                        temp.put(name, substitute);
                        routineArray.remove(i);
                        routineArray.put(i, temp);
                        for(int j = i + 1; j <tempArray.length();j++){
                            routineArray.put(j,tempArray.get(j));
                        }

                    }
                }
                alarmCollections.setRoutineArray(routineArray);
                alarmCollections.createJSON(context, "RoutineList.json");
            } catch (Exception e) {

            }
        }else if (type.equals("POST")) {
            createNotification(context, name + " Finished!", "Touch here!", name + " Finished!");
            try {
                //Attempt to store time from alarm.
                for (int i = 0; i < routineArray.length(); i++) {
                    JSONObject temp = (JSONObject) routineArray.get(i);
                    if (temp.has(name)) {
                        JSONArray substitute = (JSONArray) temp.get(name);
                        substitute.remove(6);
                        substitute.put(6, time);
                        temp.remove(name);
                        temp.put(name, substitute);
                        routineArray.remove(i);
                        routineArray.put(i, temp);
                        for(int j = i + 1; j <tempArray.length();j++){
                            routineArray.put(j,tempArray.get(j));
                        }

                    }
                }
                alarmCollections.setRoutineArray(routineArray);
                alarmCollections.createJSON(context, "RoutineList.json");
            } catch (Exception e) {

            }
        }



    }
    private void createEmptyNotification(Context context, String s, String s1, String alert){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(s)
                .setTicker(alert)
                .setContentText(s1)
                .setSmallIcon(R.drawable.book_notification);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1,mBuilder.build());
    }

    private void createNotification(Context context, String s, String s1, String alert) {
        Intent sendName = new Intent(context, ConfirmedNotification.class);
        Bundle extras = new Bundle();
        extras.putString("NAME",name);
        extras.putLong("TIME",time);
        sendName.putExtras(extras);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,id, sendName,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(s)
                .setTicker(alert)
                .setContentText(s1)
                .setSmallIcon(R.drawable.book_notification);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id,mBuilder.build());
    }
}
