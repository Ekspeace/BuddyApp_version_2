package com.ekspeace.buddyapp_v2.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStateManagerControl;
import androidx.fragment.app.FragmentTransaction;

import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentFour;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentOne;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentThree;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentTwo;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BookingFragmentOne.getInstance();
            case 1:
                return BookingFragmentTwo.getInstance();
            case 2:
                return BookingFragmentThree.getInstance();
            case 3:
                return BookingFragmentFour.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

}