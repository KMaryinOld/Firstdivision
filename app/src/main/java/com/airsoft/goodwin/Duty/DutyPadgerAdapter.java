package com.airsoft.goodwin.Duty;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.Map;

public class DutyPadgerAdapter extends FragmentStatePagerAdapter {
    private Map<Integer, CurrentDutyUsersTabFragment> fragments;

    public DutyPadgerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new HashMap<>();
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments.size() == 3) {
            return fragments.get(position + 1);
        } else {
            return new Fragment();
        }
    }

    public void setCoyDutyTabFragment(CurrentDutyUsersTabFragment coyDutyTabFragment, int type) {
        fragments.put(type, coyDutyTabFragment);

        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
