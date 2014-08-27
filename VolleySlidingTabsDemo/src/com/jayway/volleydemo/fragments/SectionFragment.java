
package com.jayway.volleydemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jayway.volleydemo.R;
import com.jayway.volleydemo.adapters.GridLayoutAdapter;
import com.jayway.volleydemo.customviews.ExpandableGridLayout;

public class SectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_LIST_ID = "section_number";

    protected static final String LOG_TAG = SectionFragment.class.getCanonicalName();

    private GridLayoutAdapter adapter;

    public SectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expandable_grid_layout, container, false);
        ExpandableGridLayout expandableGridLayout = (ExpandableGridLayout) view
                .findViewById(R.id.expandable_grid_layout);

        adapter = new GridLayoutAdapter(getActivity(), getArguments().getInt(ARG_LIST_ID));
        expandableGridLayout.setAdapter(adapter);
        expandableGridLayout.forceLayout();
        return view;
    }

}
