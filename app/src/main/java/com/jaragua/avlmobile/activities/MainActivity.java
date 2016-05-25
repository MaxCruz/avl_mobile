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

/**
 * Main activity for the application. Implements configuration and messages fragments
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.container)
    protected NonSwipeableViewPager viewPager;
    @Bind(R.id.tabs)
    protected TabLayout tabLayout;

    /**
     * Create the activity:
     * 1. Bind the layout using BUtterKnife
     * 2. Configure the toolbar
     * 3. Set the view pager
     *
     * @param savedInstanceState saved instance from previous configuration
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1. Bind the layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //2. Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_track_changes_white_36dp);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.app_description);
        setSupportActionBar(toolbar);
        //3. Set view pager
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

        @SuppressWarnings("unused") // Used with reflection
        Message(R.string.tab_message, MessageFragment.class),
        @SuppressWarnings("unused") // Used with reflection
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
