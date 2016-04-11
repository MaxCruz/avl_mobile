package com.jaragua.avlmobile.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.fragments.ConfigurationFragment;
import com.jaragua.avlmobile.fragments.MessageFragment;
import com.jaragua.avlmobile.views.NonSwipeableViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.container)
    protected NonSwipeableViewPager viewPager;
    @Bind(R.id.tabs)
    protected TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.drawable.ic_stat_action_track_changes);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.app_description);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private enum Tab {

        Message(R.string.tab_message, MessageFragment.class),
        Configuration(R.string.tab_configuration, ConfigurationFragment.class);

        int resourceTitle;
        Class<? extends Fragment> fragmentClass;

        Tab(int resourceTitle, Class<? extends Fragment> fragmentClass) {
            this.resourceTitle = resourceTitle;
            this.fragmentClass = fragmentClass;
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Tab tab = Tab.values()[position];
            try {
                return tab.fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int getCount() {
            return Tab.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Tab tab = Tab.values()[position];
            return getString(tab.resourceTitle);
        }
    }
}
