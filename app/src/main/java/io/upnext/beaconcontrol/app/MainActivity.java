/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import io.upnext.beaconcontrol.app.actions.BeaconActionsFragment;
import io.upnext.beaconcontrol.app.actions.BeaconActionsLogsStore;
import io.upnext.beaconcontrol.app.beacons.BeaconsFragment;
import io.upnext.beaconcontrol.app.beacons.BeaconsFragment.ReloadConfigurationCallback;
import io.upnext.beaconcontrol.app.config.BeaconControlConfiguration;
import io.upnext.beaconcontrol.app.info.InformationFragment;
import io.upnext.beaconcontrol.app.info.InformationFragment.LogoutCallback;
import io.upnext.beaconcontrol.sdk.Action;
import io.upnext.beaconcontrol.sdk.Beacon;
import io.upnext.beaconcontrol.sdk.BeaconControl;
import io.upnext.beaconcontrol.sdk.BeaconDelegate;
import io.upnext.beaconcontrol.sdk.BeaconErrorListener;
import io.upnext.beaconcontrol.sdk.ErrorCode;

public class MainActivity extends AppCompatActivity implements ReloadConfigurationCallback, LogoutCallback {

    private static final String TAG = "MainActivity";
    private static final String LOCATION_REQUIREMENT_EXPLANATION_DIALOG_TAG = "LOCATION_REQUIREMENT_EXPLANATION_DIALOG_TAG";
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int VIEWPAGER_OFFSCREEN_PAGE_LIMIT = 2;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    //flag required because of https://code.google.com/p/android/issues/detail?id=190966
    private boolean locationAccessPermissionDenied = false;

    private BeaconActionsLogsStore logsStore;
    private BeaconsFragment beaconsFragment;
    private BeaconControl beaconControl;
    private BeaconControlConfiguration beaconControlConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        logsStore = new BeaconActionsLogsStore(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(VIEWPAGER_OFFSCREEN_PAGE_LIMIT);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (isLocationPermissionGranted()) {
            startBeaconControl();
        }
    }

    private void startBeaconControl() {
        Log.d(TAG, "startBeaconControl");
        beaconControlConfiguration = new BeaconControlConfiguration(this);
        beaconControl = BeaconControl.getInstance(
                this,
                beaconControlConfiguration.getClientId(),
                beaconControlConfiguration.getClientSecret(),
                beaconControlConfiguration.getUserId()
        );
        beaconControl.enableLogging(true);
        beaconControl.setBeaconDelegate(new BeaconDelegate() {
            @Override
            public boolean shouldPerformActionAutomatically() {
                return true;
            }

            @Override
            public void onActionStart(Action action) {
                Log.d(TAG, "onActionStart");
                logsStore.logActionStart(action);
            }

            @Override
            public void onActionEnd(Action action) {
                Log.d(TAG, "onActionEnd");
            }

            @Override
            public void onBeaconsConfigurationLoaded(List<Beacon> list) {
                if (beaconsFragment != null) {
                    beaconsFragment.setBeacons(list);
                }
            }

            @Override
            public void onBeaconProximityChanged(Beacon beacon) {
                if (beaconsFragment != null) {
                    beaconsFragment.onBeaconProximityChanged(beacon);
                }
            }
        });
        beaconControl.setBeaconErrorListener(new BeaconErrorListener() {
            @Override
            public void onError(ErrorCode errorCode) {
                Log.e(TAG, "onError " + errorCode);
                logsStore.logError(errorCode);
            }
        });
        beaconControl.startScan();
    }

    @Override
    public void reloadConfiguration() {
        beaconControl.reloadConfiguration();
    }

    @Override
    public void logout() {
        Log.d(TAG, "Logging out");
        beaconControl.stopScan();
        beaconControlConfiguration.clear();
        logsStore.clear();
        startActivity(StartupActivity.getIntent(this));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationAccessPermissionDenied) {
            //the user could have granted the permission from the application settings
            if (!isLocationPermissionGranted()) {
                showLocationPermissionRequiredDialog();
            }
        } else {
            requestLocationPermissionIfNotGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    locationAccessPermissionDenied = true;
                } else {
                    startBeaconControl();
                }
            }
        }
    }

    private void requestLocationPermissionIfNotGranted() {
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showLocationPermissionRequiredDialog() {
        DialogFragment dialogFragment = LocationPermissionExplainingDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), LOCATION_REQUIREMENT_EXPLANATION_DIALOG_TAG);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private static final int TAB_BEACONS_POS = 0;
        private static final int TAB_ACTIONS_POS = 1;
        private static final int TAB_INFO_POS = 2;
        private static final int TAB_COUNT = 3;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_BEACONS_POS:
                    beaconsFragment = BeaconsFragment.getInstance();
                    return beaconsFragment;
                case TAB_ACTIONS_POS:
                    return BeaconActionsFragment.getInstance();
                case TAB_INFO_POS:
                    return InformationFragment.getInstance();
                default:
                    throw new IllegalStateException("Unknown adapter position " + position);
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_BEACONS_POS:
                    return getString(R.string.tab_beacons_title);
                case TAB_ACTIONS_POS:
                    return getString(R.string.tab_actions_title);
                case TAB_INFO_POS:
                    return getString(R.string.tab_info_title);
            }
            return null;
        }
    }
}
