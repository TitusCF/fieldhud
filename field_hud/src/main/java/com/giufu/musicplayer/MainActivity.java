package com.giufu.musicplayer;


import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.giufu.musicplayer.fragments.BaseFragment;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.giufu.musicplayer.fragments.MapFragment;
import com.giufu.musicplayer.fragments.MusicFragment;
import com.giufu.musicplayer.fragments.SettingsFragment;
import com.giufu.musicplayer.fragments.SummaryFragment;
import com.giufu.musicplayer.model.MusicItem;
import com.giufu.musicplayer.services.MusicService;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

import kotlin.random.Random;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity{

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    public static SharedPreferences sharedPreferences;
    public static Resources resources;

    private final SummaryFragment summaryFragment = SummaryFragment.Companion.newInstance(R.menu.music_menu);
    private final SettingsFragment settingsFragment = SettingsFragment.Companion.newInstance(R.menu.settings_menu);
    //private final MapFragment mapFragment = MapFragment.Companion.newInstance(R.menu.map_menu);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        resources = getResources();
        final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
            getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(screenSlidePagerAdapter);

        fragments.add(summaryFragment);
        fragments.add(settingsFragment);
        //fragments.add(mapFragment);

        screenSlidePagerAdapter.notifyDataSetChanged();
        final TabLayout tabLayout = findViewById(R.id.page_indicator);
        tabLayout.setupWithViewPager(viewPager, true);
    }
    @Override

    protected void onResume() {super.onResume();}

    @Override
    public boolean onGesture(Gesture gesture) {
        switch (gesture) {
            case TAP:
                fragments.get(viewPager.getCurrentItem()).onSingleTapUp();
                return true;
            default:
                return super.onGesture(gesture);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            summaryFragment.onDestroy();
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
