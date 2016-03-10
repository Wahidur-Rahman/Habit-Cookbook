package com.example.wahidur.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wahidur.routine_structure.Routine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by Alessandro on 19/01/2016.
 * Adapted from the alarm manager demo on
 * https://developer.android.com/training/scheduling/alarms.html
 */
public class AlarmSetup {
    public static void setAlarms(Context context){


        //Need to extra the JSON Time data for setting alarms.

        ArrayList<Integer> endHour = new ArrayList<Integer>();
        ArrayList<Integer> endMinute = new ArrayList<Integer>();
        ArrayList<Integer> startHour = new ArrayList<Integer>();
        ArrayList<Integer> startMinute = new ArrayList<Integer>();
        ArrayList<Integer> notificationNumber = new ArrayList<Integer>();
        ArrayList<Long> previousEndTime = new ArrayList<Long>();
        ArrayList<Long> previousStartTime = new ArrayList<Long>();
        ArrayList<String> namesOfRoutine = new ArrayList<String>();

        Routine alarmCollection = new Routine();
        alarmCollection.attemptListCreation(context, "RoutineList.json");
        JSONArray routineArray = alarmCollection.getRoutineArray();
        Log.w("ROUTINE ARRAY", routineArray.toString());

        //iterating over List to get alarms.
        for(int i = 0; i <routineArray.length();i++){
            try {
                JSONObject routineObject = (JSONObject) routineArray.get(i);
                Iterator forJSONObject = routineObject.keys();
                String name = (String) forJSONObject.next();
                namesOfRoutine.add(name);
                JSONArray arrayTimes = (JSONArray) routineObject.get(name);
                ArrayList<String> routineTime = alarmCollection.jsonArrayToArrayList(arrayTimes);
                startHour.add(Integer.valueOf(routineTime.get(0)));
                startMinute.add(Integer.valueOf(routineTime.get(1)));
                endHour.add(Integer.valueOf(routineTime.get(2)));
                endMinute.add(Integer.valueOf(routineTime.get(3)));
                notificationNumber.add(Integer.valueOf(routineTime.get(4)));
                previousStartTime.add(Long.valueOf(routineTime.get(5)));
                previousEndTime.add(Long.valueOf(routineTime.get(6)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Cancelling previous alarms to update them.


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for(int j = 0; j <1000; j++){
            Intent deletingIntents = new Intent(context, AlarmReceiver.class);
            PendingIntent deletingPending = PendingIntent.getBroadcast(context, j, deletingIntents, PendingIntent.FLAG_UPDATE_CURRENT);
            try{
                alarmManager.cancel(deletingPending);
            } catch (Exception e){
                //no alarm set to cancel
            }
        }

        for (int i = 0; i < endHour.size()*2; i++) {
            long offset = 24*60*60*1000;
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            Bundle extras = new Bundle();
            extras.putInt("ID", i);
            if(i<endHour.size()){
                extras.putString("NAME", namesOfRoutine.get(i));
                extras.putString("TYPE", "PRE");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, startHour.get(i));
                calendar.set(Calendar.MINUTE, startMinute.get(i));
                calendar.set(Calendar.SECOND, 0);
                long time = calendar.getTimeInMillis();

                //Setting rate
                int rate = getRate(notificationNumber.get(i));
                if(previousStartTime.get(i) != 0){
                    time = previousStartTime.get(i);
                }
                while(time < Calendar.getInstance().getTimeInMillis() + 5000){
                    time += offset*rate;
                }
                extras.putLong("TIME",time);
                alarmIntent.putExtras(extras);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,offset*rate, pendingIntent);
            }else{
                int j = i - endHour.size();
                extras.putString("NAME",namesOfRoutine.get(j));
                extras.putString("TYPE","POST");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, endHour.get(j));
                calendar.set(Calendar.MINUTE, endMinute.get(j));
                calendar.set(Calendar.SECOND, 0);
                long time = calendar.getTimeInMillis();

                //Setting Rate
                int rate = getRate(notificationNumber.get(j));
                if(previousEndTime.get(j) != 0){
                    time = previousEndTime.get(j);
                }
                while(time < Calendar.getInstance().getTimeInMillis() + 5000){
                    time += offset*rate;
                }
                extras.putLong("TIME",time);
                alarmIntent.putExtras(extras);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, offset*rate, pendingIntent);
            }

        }

    }

    /**
     *
     * getRate() determines how often to repeat the alarm. The rate is multiplied by an offset of
     * 24 hours. For example a rate of 3 corresponds to the alarm repeating every 24*3= 72 hours.
     * Adjust the rate and boundary conditions as needed. Set a final rate appropriate for someone
     * who has 'formed' a habit.
     *
     */
    public static int getRate(Integer count){
        int rate = 1;
        if(count <= 1){
            rate = 1;
        }else if(count >1 && count <= 2){
            rate = 2;
        }else if(count > 2){
            rate = 3;
        }
        return rate;
    }
}
