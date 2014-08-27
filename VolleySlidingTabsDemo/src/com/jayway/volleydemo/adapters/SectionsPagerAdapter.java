
package com.jayway.volleydemo.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jayway.volleydemo.data.Repository;
import com.jayway.volleydemo.fragments.SectionFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int listID) {
        Fragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putInt(SectionFragment.ARG_LIST_ID, listID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (Repository.getLists() != null) {
            return Repository.getLists().size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Repository.getListTitle(position);
    }
}
