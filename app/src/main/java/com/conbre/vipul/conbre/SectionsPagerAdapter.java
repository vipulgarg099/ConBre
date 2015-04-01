package com.conbre.vipul.conbre;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by Vipul on 30-03-2015.
 *  /**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;



    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
       switch(position) {
           case 0:
               return new inboxFragment();
           case 1:
               return new friendsFragment();
       }
        return null;
    }

    @Override
    public int getCount() {

        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.inbox_section).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.friends_section).toUpperCase(l);
            }
        return null;
    }


}
