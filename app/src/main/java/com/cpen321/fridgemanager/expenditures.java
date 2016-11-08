package com.cpen321.fridgemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.cpen321.fridgemanager.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class expenditures extends Fragment{

    List<String> ChildList;
    Map<String, List<String>> ParentListItems;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    // parent list items
    List<String> ParentList = new ArrayList<String>();
    { ParentList.add("JANUARY");
        ParentList.add("FEBRUARY");
        ParentList.add("MARCH");
    }

    // children list items
    String[] JanItems = {"1", "2", "3"};
    String[] FebItems = {"11", "22", "33"};
    String[] MarItems = {"111", "222", "333"};
    String[] DefaultMsg = {"loading"};



    public expenditures() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: check
        //setContentView(R.layout.fragment_expenditures);

        ParentListItems = new LinkedHashMap<String, List<String>>();

        for ( String HoldItem: ParentList ) {
            if (HoldItem.equals("JANUARY")) {
                loadChildItem(JanItems);
            }
            else if (HoldItem.equals("FEBRUARY")) {
                loadChildItem(FebItems);
            }
            else if (HoldItem.equals("MARCH")) {
                loadChildItem(MarItems);
            }
            else {
                loadChildItem(DefaultMsg);
            }

            ParentListItems.put(HoldItem, ChildList);
        }
/*
        expandableListView = (ExpandableListView) ;
        expandableListAdapter= new ListAdapter(this, ParentList, ParentListItems);

        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                final String selected = (String) expandableListAdapter.getChild(
                        groupPosition, childPosition);
                //Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();

                return true;
            }
        });

        */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expenditures, container, false);
        expandableListView = (ExpandableListView) root.findViewById(R.id.list);
        // Inflate the layout for this fragment
        return root;
    }

    private void loadChildItem(String[] ParentElementsName) {
        ChildList = new ArrayList<String>();
        for (String model: ParentElementsName)
            ChildList.add(model);
    }


}