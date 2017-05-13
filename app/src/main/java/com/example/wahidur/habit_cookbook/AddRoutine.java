package com.example.wahidur.habit_cookbook;
import com.example.wahidur.fragments.*;
import com.example.wahidur.routine_structure.Routine;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.wahidur.fragments.AnalyticsApplication;
import com.example.wahidur.routine_structure.Routine;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

public class AddRoutine extends FragmentActivity {
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String routineNew;
    private String parentName;
    private String value;
    private Tracker mTracker;
    private Button delete;
    private Button confirmButton;
    private Button startTime;
    private Button endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);


        startHour = 0;
        startMinute = 0;
        endHour = 1;
        endMinute = 0;


        TextView validation = (TextView) findViewById(R.id.textValidateConfirm);
        validation.setVisibility(View.INVISIBLE);


        confirmButton = (Button) findViewById(R.id.confirmRoutineButton);
        startTime = (Button) findViewById(R.id.buttonStartTime);
        endTime = (Button) findViewById(R.id.buttonEndTime);
        delete = (Button) findViewById(R.id.buttonDelete);
        delete.setVisibility(View.INVISIBLE);

        unpackIntents();

        setListeners();


    }

    /**
     * Method to unpack the various intents that arrive at this screen.
     * The way the unpacking occurs depends on which 'OPERATION' value is passed to it.
     */
    private void unpackIntents(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                value = extras.getString("OPERATION");
                if(value.equals("START")){
                    TextView start = (TextView) findViewById(R.id.textStartView);
                    TextView end = (TextView) findViewById(R.id.textEndView);
                    startHour = extras.getInt("Hour");
                    String hour = Integer.toString(startHour);

                    if(hour.length()<2){
                        hour = "0" + hour;
                    }
                    startMinute = extras.getInt("Minute");
                    String minute = Integer.toString(startMinute);
                    if(minute.length()<2){
                        minute = "0" + minute;
                    }
                    start.setText(hour + ":" + minute);
                    end.setText(extras.getString("FINAL"));
                } else if (value.equals("END")){
                    TextView end = (TextView) findViewById(R.id.textEndView);
                    TextView start = (TextView) findViewById(R.id.textStartView);
                    endHour = extras.getInt("Hour");
                    String hour = Integer.toString(endHour);
                    if(hour.length()<2){
                        hour = "0" + hour;
                    }
                    endMinute = extras.getInt("Minute");
                    String minute = Integer.toString(endMinute);
                    if(minute.length()<2){
                        minute = "0" + minute;
                    }
                    end.setText(hour + ":" + minute);
                    start.setText(extras.getString("INITIAL"));
                }else if (value.equals("EDIT")){
                    TextView parentList = (TextView) findViewById(R.id.editRoutineText);
                    delete.setVisibility(View.VISIBLE);
                    parentName = extras.getString("ROUTINE");
                    parentList.setText(parentName);
                    Routine temp = new Routine();
                    temp.attemptListCreation(this, "RoutineList.json");
                    JSONArray tempRoutinesArray = temp.getRoutineArray();
                    for(int i = 0 ;i <tempRoutinesArray.length(); i++){
                        JSONObject tempRoutine = tempRoutinesArray.getJSONObject(i);
                        if(tempRoutine.has(parentName)){
                            ArrayList<String> times = temp.jsonArrayToArrayList((JSONArray) tempRoutine.get(parentName));
                            TextView end = (TextView) findViewById(R.id.textEndView);
                            TextView start = (TextView) findViewById(R.id.textStartView);
                            start.setText(times.get(0) + ":" + times.get(1));
                            end.setText(times.get(2) + ":" + times.get(3));
                        }
                    }
                }

            } catch(Exception e){
                //Error
            }
        }
    }

    /**
     * Method to set the Listeners for all the icons on the screen
     */
    private void setListeners(){

        //for when they confirm their entry
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView routineName = (TextView) findViewById(R.id.editRoutineText);
                routineNew = routineName.getText().toString();
                TextView validation = (TextView) findViewById(R.id.textValidateConfirm);
                TextView initialTime = (TextView) findViewById(R.id.textStartView);
                TextView finalTime = (TextView) findViewById(R.id.textEndView);

                //making sure they did not leave the field blank
                if (routineNew.length() == 0) {
                    validation.setVisibility(View.VISIBLE);

                } else {
                    //method if the operation is an edit of a prexisting routine
                    if(value != null && value.equals("EDIT")){
                        TextView end = (TextView) findViewById(R.id.textEndView);
                        TextView start = (TextView) findViewById(R.id.textStartView);
                        Routine temp = new Routine();
                        temp.attemptListCreation(AddRoutine.this, "RoutineList.json");
                        JSONArray tempRoutinesArray = temp.getRoutineArray();
                        Log.w("JSON ARRAY", tempRoutinesArray.toString());
                        Map<String, ArrayList<String>> mapCollection = temp.getHabitCollection();
                        ArrayList<String> medCollection = new ArrayList<String>();
                        medCollection = mapCollection.get(parentName);
                        mapCollection.remove(parentName);
                        temp.setHabitCollection(mapCollection);
                        ArrayList<String> parentList = temp.getGroupList();
                        parentList.remove(parentName);
                        temp.setGroupList(parentList);
                        //medCollection.remove(medCollection.size()-1);
                        for(int i = 0; i<tempRoutinesArray.length();i++){
                            try {
                                JSONObject tempRoutine = tempRoutinesArray.getJSONObject(i);
                                if(tempRoutine.has(parentName)){
                                    tempRoutinesArray.remove(i);
                                    temp.setRoutineArray(tempRoutinesArray);
                                }


                            }catch(Exception e){
                                //couldn't get object
                            }
                        }
                        try {
                            JSONArray initial = new JSONArray(Arrays.asList(((String) start.getText()).split(":")));
                            JSONArray finals = new JSONArray(Arrays.asList(((String) end.getText()).split(":")));
                            temp.newEditedRoutine(routineNew, initial, finals, medCollection);
                            temp.createJSON(AddRoutine.this, "RoutineList.json");
                            Intent intent = new Intent(AddRoutine.this, HomeScreen.class);
                            startActivity(intent);
                        }catch(Exception e){

                        }

                    }else{
                        //Method if the operation is to add a new routine
                        Routine tempCollection = new Routine();
                        tempCollection.attemptListCreation(AddRoutine.this, "RoutineList.json");
                        ArrayList<String> tempList = tempCollection.getGroupList();
                        if(tempList.contains(routineNew)){
                            TextView validateEdit = (TextView) findViewById(R.id.textValidateConfirm);
                            validateEdit.setText("This routine name already exists. Please try a different name.");
                            validateEdit.setVisibility(View.VISIBLE);
                        }else{
                            Intent intent = new Intent(AddRoutine.this, HomeScreen.class);
                            Bundle extras = new Bundle();
                            extras.putString("New Routine", routineNew);
                            extras.putString("OPERATION","ADD ROUTINE");
                            extras.putString("INITIAL", (String) initialTime.getText());
                            extras.putString("FINAL",(String) finalTime.getText());
                            intent.putExtras(extras);
                            startActivity(intent);
                        }


                    }
                }

            }
        });
        //Brings up a Time Picker dialog box for them to set the time.
        startTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showTimePickerDialog(v);
                final TextView textStart = (TextView) findViewById(R.id.textStartView);
                TextView textEnd = (TextView) findViewById(R.id.textEndView);
                String initialTime = (String) textStart.getText();
                String finalTime = (String) textEnd.getText();
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddRoutine.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String startHourText = String.valueOf(selectedHour);
                        String startMinuteText = String.valueOf(selectedMinute);
                        if(startHourText.length()<2){
                            startHourText = "0" + startHourText;
                        }
                        if(startMinuteText.length()<2){
                            startMinuteText = "0" + startMinuteText;
                        }
                        textStart.setText(startHourText + ":" + startMinuteText);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        //Brings up a Time Picker dialog box for them to set the end time.
        endTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showTimePickerDialog(v);
                TextView textStart = (TextView) findViewById(R.id.textStartView);
                final TextView textEnd = (TextView) findViewById(R.id.textEndView);
                String initialTime = (String) textStart.getText();
                String finalTime = (String) textEnd.getText();
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddRoutine.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String endHourText = String.valueOf(selectedHour);
                        String endMinuteText = String.valueOf(selectedMinute);
                        if(endHourText.length()<2){
                            endHourText = "0" + endHourText;
                        }
                        if(endMinuteText.length()<2){
                            endMinuteText = "0" + endMinuteText;
                        }
                        textEnd.setText(endHourText + ":" + endMinuteText);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //Allows you to delete a routine (only visible when editing a pre-existing one)
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddRoutine.this);
                builder.setMessage("This will delete all associated medications. Are you sure?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Routine tempCollection = new Routine();
                                tempCollection.attemptListCreation(AddRoutine.this, "RoutineList.json");
                                JSONArray tempList = tempCollection.getRoutineArray();
                                for (int i = 0; i < tempList.length(); i++) {
                                    try {
                                        JSONObject tempObject = (JSONObject) tempList.get(i);
                                        tempObject.remove(parentName);
                                        if (tempObject.length() == 0) {
                                            tempList.remove(i);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                JSONObject tempMeds = tempCollection.getMedications();
                                tempMeds.remove(parentName);
                                tempCollection.setMedications(tempMeds);
                                tempCollection.setRoutineArray(tempList);
                                tempCollection.updateRoutineList();
                                try {
                                    tempCollection.createJSON(AddRoutine.this,"RoutineList.json");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.cancel();

                                Intent intent = new Intent(AddRoutine.this, HomeScreen.class);
                                startActivity(intent);
                            }

                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }


}
