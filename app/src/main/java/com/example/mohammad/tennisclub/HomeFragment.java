package com.example.mohammad.tennisclub;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Label;

public class HomeFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Set click listener to call method createBooking
        rootView.findViewById(R.id.fab_normal).setOnClickListener(this);
        rootView.findViewById(R.id.fab_private).setOnClickListener(this);
        rootView.findViewById(R.id.fab_group).setOnClickListener(this);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        final FloatingActionMenu fab = rootView.findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (tab.getPosition() == 0) {
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    fab.setVisibility(View.VISIBLE);
                    fab.startAnimation(fadeIn);
                } else {
                    Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                    fab.startAnimation(fadeOut);
                    fab.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_home);
    }

    /**
     * Show snack bar message of fab item clicked
     *
     * @param v
     */
    public void createBooking(View v) {
        String sessionType;
        if (v instanceof Label) {
            sessionType = ((Label) v).getText().toString();
        } else {
            sessionType = ((FloatingActionButton) v).getLabelText();
        }
        Snackbar.make(v, sessionType, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        createBooking(v);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new BookingTabFragment().newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
