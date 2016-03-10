package com.example.wahidur.habit_cookbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;


import com.example.wahidur.fragments.AlarmSetup;
import com.example.wahidur.fragments.AnalyticsApplication;
import com.example.wahidur.fragments.ExpandableListAdapter;
import com.example.wahidur.routine_structure.Routine;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.*;

public class HomeScreen extends Activity {

    private ArrayList<String> groupList;
    private ArrayList<String> childList;
    private JSONArray jsonArray;
    private Map<String, ArrayList<String>> habitCollection;
    private Routine newCollection;
    private ExpandableListView expListView;
    private ExpandableListAdapter expListAdapter;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        groupList = new ArrayList<String>();
        habitCollection = new LinkedHashMap<String, ArrayList<String>>();

        Button routineButton = (Button) findViewById(R.id.addRoutineButton);
        routineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, AddRoutine.class));
            }
        });


        // Initialising the lists for the list adapter
        newCollection = new Routine();
        newCollection.attemptListCreation(this, "RoutineList.json");
        groupList = newCollection.getGroupList();
        habitCollection = newCollection.getHabitCollection();


        //creating and expanding the List Adapter and populating it.
        expListView = (ExpandableListView) findViewById(R.id.routine_list);
        expListAdapter = new ExpandableListAdapter(this, groupList, habitCollection);
        expListView.setAdapter(expListAdapter);

        Bundle extras = getIntent().getExtras();
        unpackIntents(extras);
        expListAdapter.notifyDataSetChanged();
        //once the list has been populated an attempt at storing the data list in a text file is below.
        try {
            newCollection.createJSON(this, "RoutineList.json");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Send the json string to analytics.
        String jsonString = newCollection.getJSONStringer();
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Home Screen");
        mTracker.send(new HitBuilders.EventBuilder()
                .set("JSON STRING: ",jsonString)
                .build());


        TextView routines = (TextView) findViewById(R.id.textAlertView);

        if (groupList.size() != 0) {
            routines.setText("Click on the list items to expand them and see your associated medications");
        } else {
            routines.setText("No routines set up. Consider adding some via the 'Add Routine' button below.");
        }

        //Setting Listeners
        setChildListener();


        AlarmSetup.setAlarms(HomeScreen.this);
    }


    /**
     * Method for defining the all the listeners on the Home Screen.
     */

    private void setChildListener(){
        expListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                if (selected.equals("Add New Habit")) {
                    //Creating an Intent Bundle to pass over to the Add Medication Screen.
                    Intent intent = new Intent(HomeScreen.this, AddMedication.class);
                    Bundle extras = new Bundle();
                    extras.putString("OPERATION","ADD");
                    extras.putInt("ParentPosition", groupPosition);
                    intent.putExtras(extras);
                    Log.w("GOT","HERE1");
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    /**
     * When the user arrives at the screen from the Add Routine or Add medication screen
     * the data from them needs to be unpacked from the intent. This is done below.
     */
    private void unpackIntents(Bundle extra){
        if (extra != null) {
            try {
                if (extra.getString("OPERATION").equals("ADD MED")) {
                    try {
                        String Medications = extra.getString("MEDICATION");
                        int parentPosition = extra.getInt("ParentPosition");
                        String parent = groupList.get(parentPosition);
                        ArrayList<String> childs = new ArrayList<String>();
                        childs = habitCollection.get(parent);
                        childs.add((childs.size() - 1), Medications);
                        for (String routine : groupList) {
                            if (routine.equals(parent)) {
                                loadChild(childs);
                            }
                        }
                    } catch (Exception t) {
                        //Error
                    }
                } else if (extra.getString("OPERATION").equals("ADD ROUTINE")) {

                    try {
                        String value = extra.getString("New Routine");
                        JSONObject tempObject = new JSONObject();
                        JSONArray tempInitial;
                        JSONArray tempFinal;
                        tempInitial = new JSONArray(Arrays.asList(extra.getString("INITIAL").split(":")));
                        tempFinal = new JSONArray(Arrays.asList(extra.getString("FINAL").split(":")));
                        newCollection.newRoutine(value,tempInitial,tempFinal);
                    } catch (Exception e) {
                        //Error
                    }
                }
            } catch (Exception f){
                //Error
            }
        }
    }

    /**
     * Use to help unpack the intents
     *
     */
    private void loadChild(ArrayList<String> habits) {
        childList = new ArrayList<String>();
        for (String model : habits)
            childList.add(model);
    }
    @Override
    public void onBackPressed() {

        //Do nothing, disabling back button here.

    }


}