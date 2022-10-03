package com.tutlane.bakalarka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/*
    Reprezentuje okno aplikace se seznamem pohybu, ktere se objevi pri stisknuti tlacitka OPEN MOVES LIST v okne StartPage.
    Seznam pohybu je rozdeleny podle stylu.
    Kazdy styl je exemplarem tridy Fragment, coz je soucasti tridy viewPagerAdapter.
*/

public class MovesList extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private AllFragment allFragment;
    private BalletFragment balletFragment;
    private MacarenaFragment macarenaFragment;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moves_list, container, false);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.moves_table_layout);

        allFragment = new AllFragment();
        balletFragment = new BalletFragment();
        macarenaFragment = new MacarenaFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager(), 0);

        viewPagerAdapter.addFragment(allFragment, "All");
        viewPagerAdapter.addFragment(balletFragment, "Ballet");
        viewPagerAdapter.addFragment(macarenaFragment, "Macarena");
        viewPager.setAdapter(viewPagerAdapter);

        return view;
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmTitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmTitles.get(position);
        }
    }
}
