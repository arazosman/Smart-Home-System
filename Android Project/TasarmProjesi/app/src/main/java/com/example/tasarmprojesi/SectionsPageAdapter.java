package com.example.tasarmprojesi;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPageAdapter extends FragmentPagerAdapter
{
    private final List<Pair<Fragment, String>> fragments = new ArrayList<>();

    public SectionsPageAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title)
    {
        fragments.add(new Pair<Fragment, String>(fragment, title));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return fragments.get(position).second;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position).first;
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }
}
