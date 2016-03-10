package com.example.wahidur.routine_structure;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.wahidur.fragments.AlarmSetup;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Wahidur on 14/01/2016.
 */
public class Routine implements Parcelable {
    private ArrayList<String> groupList;
    private ArrayList<String> childList;
    private JSONArray jsonArray;
    private Map<String, ArrayList<String>> habitCollection;
    private JSONObject medications;
    private Tracker mTracker;
    private String jsonStringer;

    /**
     * The parcel allows us to send off large datasets at once in intents.
     * Not currently used, but implemented for future reasons.
     */
    Routine(Parcel in){
    }
    public Routine(){
    }

    /**
     * There is an odd quirk where in org.json library writing to a string causes the ArrayList to
     * be surrounded by quotes and hence treated as one single String object when being read again rather than an Array.
     * Can't be cast to normal ArrayList even if they have the same data type.
     * This utillity method was created to help with that.
     */
    private String ArrayListToString(ArrayList<String> list){
        //method required as part of the string writing in the createJSON method.
        String output = "[";
        for(int i = 0; i < list.size(); i++){
            output = output + "\"" + list.get(i) + "\"";
            if(i<list.size()-1){
                output = output + ",";
            }
        }
        output = output + "]";
        return output;
    }
    public void attemptListCreation(Context c, String FILENAME){
        //Method to try read a RoutineList.json file in internal storage that can be edited. If not
        //present then a RoutineList.json file is created.
        try {
            File file = new File(c.getFilesDir(), FILENAME);
            FileReader habitData = new FileReader(file.getAbsolutePath().toString());
            BufferedReader reader = new BufferedReader(habitData);
            String jsonString = reader.readLine();
            try {
                JSONObject json = new JSONObject(jsonString);
                groupList = new ArrayList<String>();
                jsonArray = (JSONArray)json.get("Routine Name");
                medications = (JSONObject)json.get("Medications");
                for(int i =0; i < jsonArray.length(); i++){
                    JSONObject temp = new JSONObject();
                    temp = (JSONObject) jsonArray.get(i);
                    Iterator<String> keys = temp.keys();
                    groupList.add(keys.next());
                }
                //groupList = jsonArrayToArrayList(jsonArray);
                JSONObject medications = (JSONObject)json.get("Medications");
                habitCollection = new LinkedHashMap<String, ArrayList<String>>();
                for(int i = 0; i < groupList.size();i++){
                    String routineName = groupList.get(i);
                    JSONArray jsonArrayChild = (JSONArray)medications.get(routineName);
                    ArrayList<String> childMeds = jsonArrayToArrayList(jsonArrayChild);

                    for(String routine: groupList){
                        if(routine.equals(routineName)){
                            loadChild(childMeds);
                        }
                    }
                    habitCollection.put(routineName, childList);
                }
            } catch (Exception f) {
                //Couldn't parse JSON
            }
        } catch (Exception e) {
            //Couldn't find JSON so create an empty JSON object to set up routines
            String string = "{\"Routine Name\":[],\"Medications\":{}}";

            FileOutputStream fos;
            try {
                fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(string.getBytes());
                fos.close();
                attemptListCreation(c,FILENAME);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }
    private void loadChild(ArrayList<String> habits) {
        childList = new ArrayList<String>();
        for (String model : habits)
            childList.add(model);
    }
    public ArrayList<String> jsonArrayToArrayList(JSONArray jsonArr){
        ArrayList<String> arrList= new ArrayList();
        if (jsonArr != null) {
            int len = jsonArr.length();
            for (int i=0;i<len;i++){
                try {
                    arrList.add(jsonArr.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrList;
    }
    public void createJSON(Context c,String FILENAME) throws JSONException {
        //This method takes the data stored in the list adapter and creates a json file in internal storage.
        String jsonwritable = "{";
        jsonwritable = jsonwritable + "\"Routine Name\":";
        jsonwritable = jsonwritable + jsonArray.toString() + ",";
        jsonwritable = jsonwritable + "\"Medications\":{";
        JSONObject writable = new JSONObject();
        writable.put("Routine Name",groupList);
        JSONObject medications = new JSONObject();
        for(int i = 0; i <groupList.size(); i++){
            String routinestemp = groupList.get(i);
            ArrayList<String> child = habitCollection.get(routinestemp);
            medications.put(routinestemp,child);
            jsonwritable = jsonwritable + "\"" + routinestemp + "\":";
            jsonwritable = jsonwritable + ArrayListToString(child);
            if( i != groupList.size() -1){
                jsonwritable = jsonwritable + ",";
            }
        }
        jsonwritable = jsonwritable + "}}";
        jsonStringer = jsonwritable;
        Log.w("JSON WRITABLE",jsonwritable);
        writable.put("Medication", medications);
        FileOutputStream fos;
        try {

            fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(jsonwritable.getBytes());
            fos.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            File file = new File(c.getFilesDir(), FILENAME);
            FileReader habitData = new FileReader(file.getAbsolutePath().toString());
            BufferedReader reader = new BufferedReader(habitData);
            String jsonString = reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //alarm code. Temporarily commented out until it is properly working.
        //AlarmSetup.setAlarms(c);
    }

    //getters and setters for various data types
    public Map getHabitCollection(){
        return habitCollection;
    }
    public ArrayList getGroupList(){
        return groupList;
    }
    public void setHabitCollection(Map<String, ArrayList<String>> habitsMap){
        this.habitCollection = habitsMap;
    }
    public void setGroupList(ArrayList<String> routines){
        this.groupList = routines;
    }
    public JSONArray getRoutineArray(){
        return jsonArray;
    }

    public void setRoutineArray(JSONArray routineArray){
        this.jsonArray = routineArray;
    }
    public JSONObject getMedications(){
        return medications;
    }
    public void setMedications(JSONObject meds){
        this.medications = meds;
    }
    //updates the RoutineList. Utility method.
    public void updateRoutineList(){
        ArrayList<String> tempGroupList = new ArrayList<String>();
        if(medications.length() != 0){
            for( int i =0; i <medications.names().length();i++){
                try {
                    tempGroupList.add(medications.names().getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Collections.reverse(tempGroupList);
            setGroupList(tempGroupList);
        }

    }
    //Adds a new medication to an associated Routine
    public void addMedToRoutine(String medication, String Routine){
        ArrayList<String> childs = new ArrayList<String>();
        childs = habitCollection.get(Routine);
        childs.add((childs.size() - 1), medication);
        for (String routine : groupList) {
            if (routine.equals(Routine)) {
                loadChild(childs);
            }
        }
    }
    //Adds a new routine to the data structure.
    public void newRoutine(String Routine, JSONArray start, JSONArray end) throws JSONException {
        groupList.add(Routine);
        ArrayList<String> childs = new ArrayList<String>();
        childs.add("Add New Habit");
        for (String routine : groupList) {
            if (routine.equals(Routine)) {
                loadChild(childs);
            }
        }
        JSONObject tempObject = new JSONObject();

        start.put(end.get(0));
        start.put(end.get(1));
        start.put(1);
        start.put(0);
        start.put(0);
        tempObject.put(Routine, start);
        jsonArray.put(tempObject);
        habitCollection.put(Routine, childList);
    }
    public void newEditedRoutine(String Routine, JSONArray start, JSONArray end,ArrayList<String> medications) throws JSONException {
        groupList.add(Routine);
        ArrayList<String> childs = new ArrayList<String>();
        childs.addAll(medications);
        for (String routine : groupList) {
            if (routine.equals(Routine)) {
                loadChild(childs);
            }
        }
        JSONObject tempObject = new JSONObject();

        start.put(end.get(0));
        start.put(end.get(1));
        start.put(1);
        start.put(0);
        start.put(0);
        tempObject.put(Routine, start);
        jsonArray.put(tempObject);
        habitCollection.put(Routine, childList);
    }
    public String getJSONStringer(){
        return jsonStringer;
    }
    public void setJSONStringer(String theJSONString){
        this.jsonStringer = theJSONString;
    }

    @Override
    public int describeContents() {
        //Object Description
        System.out.println("This class stores all routine data and provides methods to easily edit them.");
        return 0;
    }
    //parcel writer for sending this structure inside an intent.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
    
    public static final Parcelable.Creator<Routine> CREATOR
            = new Parcelable.Creator<Routine>() {
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };

}
