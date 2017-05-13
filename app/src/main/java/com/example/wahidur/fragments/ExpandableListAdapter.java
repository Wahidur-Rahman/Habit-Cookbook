package com.example.wahidur.fragments;

/**
 * Created by Wahidur on 16/12/2015.
 *
 * Adapted from a demo of the Expandable List Adapter demo found on
 * http://theopentutorials.com/tutorials/android/listview/android-expandable-list-view-example/
 *
 */
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wahidur.habit_cookbook.AddRoutine;
import com.example.wahidur.habit_cookbook.R;
import com.example.wahidur.routine_structure.Routine;

import org.json.JSONException;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, ArrayList<String>> habitCollections;
    private ArrayList<String> groupRoutines;
    private Routine newCollection;

    public ExpandableListAdapter(Activity context, ArrayList<String> groupList,
                                 Map<String, ArrayList<String>> habitCollectionMap) {
        this.context = context;
        this.habitCollections = habitCollectionMap;
        this.groupRoutines = groupList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return habitCollections.get(groupRoutines.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String med = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.childitems);
        ImageView editingChild = (ImageView) convertView.findViewById(R.id.editChild);
        if(med.equals("Add New Habit")){
            editingChild.setVisibility(View.INVISIBLE);
            item.setTypeface(null, Typeface.BOLD);
            item.setBackgroundColor(Color.parseColor("#ffdb99"));
            item.setGravity(Gravity.CENTER);
            item.setTextSize(17);
        }else{
            editingChild.setVisibility(View.VISIBLE);
            item.setTypeface(null, Typeface.NORMAL);
            item.setBackgroundColor(Color.TRANSPARENT);
            //item.setBackgroundColor(Color.parseColor("#000000"));
            item.setGravity(Gravity.CENTER_VERTICAL);
        }

        editingChild.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ArrayList<String> child =
                                        habitCollections.get(groupRoutines.get(groupPosition));
                                child.remove(childPosition);
                                notifyDataSetChanged();
                                Routine updater = new Routine();
                                updater.attemptListCreation(context, "RoutineList.json");
                                updater.setHabitCollection(habitCollections);
                                try {
                                    updater.createJSON(context,"RoutineList.json");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

        item.setText(med);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return habitCollections.get(groupRoutines.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return groupRoutines.get(groupPosition);
    }

    public int getGroupCount() {
        return groupRoutines.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        String parentName = (String) getGroup(groupPosition);
        final String parentToEdit = new String(parentName);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.Parent);
        ImageView editParent = (ImageView) convertView.findViewById(R.id.editGroup);
        editParent.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                //newCollection = new Routine();
                //ArrayList<String>
                Bundle extras = new Bundle();
                extras.putString("OPERATION","EDIT");
                extras.putString("ROUTINE",parentToEdit);
                Intent intent = new Intent(v.getContext(), AddRoutine.class);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
        item.setTypeface(null, Typeface.BOLD);
        item.setText(parentName);

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}