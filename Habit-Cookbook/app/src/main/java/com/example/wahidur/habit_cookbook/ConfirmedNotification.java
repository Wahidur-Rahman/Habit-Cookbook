package com.example.wahidur.habit_cookbook;

/**
 * This class implements the Confirmed Notification activity. Not fully complete yet.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wahidur.fragments.AlarmReceiver;
import com.example.wahidur.routine_structure.Routine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ConfirmedNotification extends AppCompatActivity {
    private Button btnNo;
    private Button btnYes;
    private ArrayList<String> medValues = new ArrayList<String>();
    private String name = "";
    private TextView medList;
    private TextView heading;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_notification);
        btnNo = (Button) findViewById(R.id.buttonNo);
        btnYes = (Button) findViewById(R.id.buttonYes);
        medList = (TextView) findViewById(R.id.textView5);
        heading = (TextView) findViewById(R.id.textView3);

        Routine alarmCollection = new Routine();
        alarmCollection.attemptListCreation(this, "RoutineList.json");
        JSONObject meds = alarmCollection.getMedications();
        Log.w("MEDICATIONS", meds.toString());
        String textList = "No habits associated with this routine.";
        try{
            Log.w("TRYING", "TO UNPACK");
            Intent intent = getIntent();
            name = intent.getStringExtra("NAME");
            Log.w("NAME Found", name);
            time = intent.getLongExtra("TIME", 0);
            Log.w("TIME FOUND",Long.toString(time));
            heading.setText("Have you taken your medication for " + name + "?");
        } catch (Exception e){
            //no intents found
        }
        try{
            Map<String, ArrayList<String>> collection = alarmCollection.getHabitCollection();
            Log.w("GOT HERE",collection.toString());
            medValues = collection.get(name);
            Log.w("GOT HERE", medValues.toString());
            if(medValues.size() <= 1){
                medList.setText(textList);
            }else{
                textList = "";
                for(int i = 0; i <medValues.size()-1; i++){
                    textList += medValues.get(i) + "\n";
                }

            }

        }catch (Exception e){
            //could not read JSON
        }

        medList.setText(textList);
    }

    /**
     *
     * This method is used to update the counter after a user presses 'Yes' or 'No'
     */

    private void updateCount(int choice){
        Routine routines = new Routine();
        routines.attemptListCreation(this, "RoutineList.json");
        JSONArray routinesArray = routines.getRoutineArray();
        JSONArray tempArray = new JSONArray();
        for(int i = 0; i <routinesArray.length();i++){
            try {
                tempArray.put(routinesArray.get(i));
            }catch (Exception e){

            }
        }
        try{
            for (int i = 0; i < routinesArray.length(); i++) {
                JSONObject temp = (JSONObject) routinesArray.get(i);
                Log.w("TEMP ARRAY 1", temp.toString());
                if (temp.has(name)) {
                    JSONArray substitute = (JSONArray) temp.get(name);
                    int countTemp = substitute.getInt(4);
                    long preTemp = substitute.getLong(5);
                    long postTemp = substitute.getLong(6);
                    if(choice == 1){
                        countTemp += choice;
                    }else if(countTemp > 1 && choice == -1){
                        countTemp += choice;
                    }
                    substitute.remove(6);
                    substitute.remove(5);
                    substitute.remove(4);
                    substitute.put(4, countTemp);
                    substitute.put(5, preTemp);
                    substitute.put(6, postTemp);
                    temp.remove(name);
                    temp.put(name, substitute);
                    routinesArray.remove(i);
                    routinesArray.put(i, temp);
                    for(int j = i + 1; j <tempArray.length();j++){
                        routinesArray.put(j,tempArray.get(j));
                    }

                }
            }
        } catch (Exception e){

        }
        routines.setRoutineArray(routinesArray);
        try {
            routines.createJSON(this, "RoutineList.json");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //
    }

    public void returnMenuWithYes(View view) {
        updateCount(1);
        startActivity(new Intent(ConfirmedNotification.this,HomeScreen.class));
    }
    public void returnMenuWithNo(View view){
        updateCount(-1);
        startActivity(new Intent(ConfirmedNotification.this,HomeScreen.class));
    }
    @Override
    public void onBackPressed() {
        //Do nothing, disabling back button here.

    }
}
