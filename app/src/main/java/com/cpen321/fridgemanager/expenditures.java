package com.cpen321.fridgemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import android.widget.TextView;


import com.cpen321.fridgemanager.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Expenditures extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expenditures, container, false);
        ExpandableListView expListView = (ExpandableListView) root.findViewById(R.id.list);
        // Inflate the layout for this fragment
        return root;
    }

    public class SavedTabsListAdapter extends BaseExpandableListAdapter {

        private String[] groups = { "Jan", "Feb", "Mar", "Apr" };

        private String[][] children = {
                { "Arnold", "Barry", "Chuck", "David" },
                { "Ace", "Bandit", "Cha-Cha", "Deuce" },
                { "Fluffy", "Snuggles" },
                { "Goldy", "Bubbles" }
        };

        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return children[i].length;
        }

        @Override
        public Object getGroup(int i) {
            return groups[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return children[i][i1];
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(Expenditures.this.getActivity());
            textView.setText(getGroup(i).toString());
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(Expenditures.this.getActivity());
            textView.setText(getChild(i, i1).toString());
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

    }
/*
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

*/

    public Expenditures() {
        // Required empty public constructor
    }

/*
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




    }
*/


/*
    private void loadChildItem(String[] ParentElementsName) {
        ChildList = new ArrayList<String>();
        for (String model: ParentElementsName)
            ChildList.add(model);
    }

*/
}