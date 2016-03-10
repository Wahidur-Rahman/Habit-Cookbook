package com.example.wahidur.habit_cookbook;
import com.example.wahidur.fragments.*;
import com.example.wahidur.routine_structure.*;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.wahidur.fragments.AnalyticsApplication;
import com.example.wahidur.fragments.ExpandableListAdapter;
import com.example.wahidur.routine_structure.Routine;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;


public class AddMedication extends AppCompatActivity {
    private int parentPosition;
    private String med;
    private String operationType;
    private ArrayList<String> groupList;
    private ArrayList<String> childList;
    private JSONArray jsonArray;
    private Map<String, ArrayList<String>> habitCollection;
    private Routine newCollection;
    private ExpandableListView expListView;
    private ExpandableListAdapter expListAdapter;
    private Tracker mTracker;
    private Button confirmMed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        confirmMed = (Button) findViewById(R.id.buttonConfirmMedication);



        TextView medValidate = (TextView) findViewById(R.id.textMedicationValidate);
        medValidate.setVisibility(View.INVISIBLE);

        unpackIntents();

        setListeners();


    }
    private void unpackIntents(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                operationType = extras.getString("OPERATION");
                if(operationType.equals("ADD")){
                    parentPosition = extras.getInt("ParentPosition");
                }else if(operationType.equals("EDIT")){
                    med = extras.getString("MEDICATION");
                    TextView setUp = (TextView) findViewById(R.id.editMedicationText);
                    setUp.setText(med);
                }

            }catch(Exception e){
                //Error
            }
        }
    }
    private void setListeners(){
        confirmMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView setUp = (TextView) findViewById(R.id.editMedicationText);
                TextView location = (TextView) findViewById(R.id.medicationText);
                TextView medValidate = (TextView) findViewById(R.id.textMedicationValidate);
                if (setUp.length() == 0) {
                    medValidate.setVisibility(View.VISIBLE);

                } else {
                    if(operationType.equals("ADD")){
                        Intent intent = new Intent(AddMedication.this, HomeScreen.class);
                        Bundle extras = new Bundle();
                        extras.putString("OPERATION", "ADD MED");
                        extras.putString("MEDICATION", setUp.getText().toString());
                        extras.putInt("ParentPosition", parentPosition);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }else if(operationType.equals("EDIT")){
                        //newCollection = new Routine();
                    }

                }
            }
        });
    }

}
