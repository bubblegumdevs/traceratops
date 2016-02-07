/*
 * Copyright 2015 Bubblegum Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bubblegum.traceratops.app.ui.activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.profiles.AppProfile;
import com.bubblegum.traceratops.app.profiles.ProfileUpdateNotifier;
import com.bubblegum.traceratops.app.service.LoggerService;
import com.bubblegum.traceratops.app.ui.Snackable;
import com.bubblegum.traceratops.app.ui.adapters.AppChooseSpinnerAdapter;
import com.bubblegum.traceratops.app.ui.fragments.DebugFragment;
import com.bubblegum.traceratops.app.ui.fragments.LoggerFragment;
import com.bubblegum.traceratops.app.ui.fragments.PingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements Snackable {

    CoordinatorLayout mCoordinatorLayout;
    android.support.v7.widget.Toolbar toolbar;
    AppCompatSpinner spinner;
    Snackbar snackbar;

    AppProfile mCurrentAppProfile = null;

    ProfileUpdateNotifier mNotifier = new ProfileUpdateNotifier(null) {
        @Override
        protected void onProfileChanged(AppProfile newProfile, AppProfile oldProfile) {
            updateUI();
        }

        @Override
        public void onEntryListUpdated(List<BaseEntry> mEntryList) {

        }

        @Override
        public void onEntryAdded(BaseEntry newEntry) {

        }

        @Override
        public void onEntriesCleared() {

        }

        @Override
        public void onNewProfileAdded(AppProfile profile) {
            if(spinner!=null) {
                updateSpinner();
            }
        }
    };

    private void updateSpinner() {
        int selectedIndex = -1, i = 0;
        final List<ApplicationInfo> profiles = new ArrayList<>();
        AppProfile currentProfile = TraceratopsApplication.from(this).getCurrentAppProfile();
        for(AppProfile profile : TraceratopsApplication.from(this).getProfiles()) {
            try {
                ApplicationInfo info = getPackageManager().getApplicationInfo(profile.targetPackageName, 0);
                profiles.add(info);
                if(currentProfile!=null && currentProfile.targetPackageName.contentEquals(info.packageName)) {
                    selectedIndex = i;
                }
                i++;
            } catch (PackageManager.NameNotFoundException e) {

            }
        }
        if(profiles.size() > 0) {
            AppChooseSpinnerAdapter spinnerAdapter = new AppChooseSpinnerAdapter(this, android.R.layout.simple_list_item_1, profiles);
            spinner.setAdapter(spinnerAdapter);
            final int lastSelectedIndex = selectedIndex;
            if(selectedIndex!=-1) {
                spinner.setSelection(lastSelectedIndex);
            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(lastSelectedIndex==position) {
                        return;
                    }
                    String packageName = profiles.get(position).packageName;
                    AppProfile profile = TraceratopsApplication.from(MainActivity.this).getProfile(packageName);
                    if (profile != null) {
                        TraceratopsApplication.from(MainActivity.this).setProfile(profile);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            getSupportActionBar().setTitle("");
        } else {
            spinner.setVisibility(View.GONE);
            getSupportActionBar().setTitle(getString(R.string.dash_board));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        spinner = (AppCompatSpinner) findViewById(R.id.navSpinner);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void updateUI() {
        updateSpinner();
        if(snackbar!=null) {
            snackbar.dismiss();
        }
        mCurrentAppProfile = TraceratopsApplication.from(this).getCurrentAppProfile();
        if(mCurrentAppProfile!=null) {
            final int errorCode = mCurrentAppProfile.getErrorCode();
            final String errorMessage = getErrorMessageIfAny(errorCode);
            if (errorMessage != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackbar = showSnackbar(errorMessage, Snackbar.LENGTH_INDEFINITE, getErrorMessageActionLabel(errorCode), getErrorMessageAction(errorCode));
                    }
                }, 300);
            }
        }
    }

    @Override
    public Snackbar showSnackbar(String message, int length, String actionLabel, View.OnClickListener action) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, length);
        if(actionLabel!=null && action!=null) {
            snackbar.setAction(actionLabel, action);
        }
        snackbar.show();
        return snackbar;
    }

    private @Nullable String getErrorMessageIfAny(int errorCode) {
        switch (errorCode) {
            case -1:
                return null;
            case LoggerService.ERROR_CODES.ERROR_CODE_APP_OUTDATED:
                return getString(R.string.error_message_app_outdated);
            case LoggerService.ERROR_CODES.ERROR_CODE_SDK_OUTDATED:
                return getString(R.string.error_message_sdk_outdated);
            case LoggerService.ERROR_CODES.ERROR_CODE_SIGNATURE_VERIFICATION_FAILED:
                return getString(R.string.error_message_signature_verification_failed);
            case LoggerService.ERROR_CODES.ERROR_CODE_SIGNATURE_VERIFICATION_FAILED_MIGHT_NEED_ACTIVATION:
                return "Trust agent is not activated yet.";
            case LoggerService.ERROR_CODES.ERROR_CODE_TRUST_AGENT_MISSING:
                return "Trust agent missing or not configured correctly.";
            default:
                return getString(R.string.error_message_unknown, errorCode);
        }
    }

    private @Nullable String getErrorMessageActionLabel(int errorCode) {
        switch (errorCode) {
            case LoggerService.ERROR_CODES.ERROR_CODE_SIGNATURE_VERIFICATION_FAILED_MIGHT_NEED_ACTIVATION:
                return "Activate";
            default:
                return null;
        }
    }

    private @Nullable View.OnClickListener getErrorMessageAction(int errorCode) {
        switch (errorCode) {
            case LoggerService.ERROR_CODES.ERROR_CODE_SIGNATURE_VERIFICATION_FAILED_MIGHT_NEED_ACTIVATION:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCurrentAppProfile!=null) {
                            confirmTrustAgent(mCurrentAppProfile.targetPackageName);
                        }
                    }
                };
            default:
                return null;
        }
    }

    private void confirmTrustAgent(String packageName) {
        String trustPackageName = packageName.concat(".trust");
        String confirmActivityName = "com.bubblegum.traceratops.trust.ui.ConfirmationActivity";
        ComponentName trustComponent = new ComponentName(trustPackageName, confirmActivityName);
        Intent confirmIntent = new Intent();
        confirmIntent.setComponent(trustComponent);
        startActivity(confirmIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearAllPendingNotifications();
        updateUI();
        TraceratopsApplication.from(this).addProfileUpdateNotifier(mNotifier);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TraceratopsApplication.from(this).removeProfileUpdateNotifier(mNotifier);
    }

    private void clearAllPendingNotifications() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(R.id.traceratops_crash_id);
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentManager fm = getSupportFragmentManager();
        TraceratopsAppViewPagerFragmentAdapter adapter = new TraceratopsAppViewPagerFragmentAdapter(fm);

        DebugFragment debugFragment = new DebugFragment();
        LoggerFragment loggerFragment = new LoggerFragment();
        PingsFragment pingsFragment = new PingsFragment();
        adapter.addFragment(loggerFragment, getString(R.string.logger_fragment_title));
        adapter.addFragment(debugFragment, getString(R.string.debug_fragment_title));
        adapter.addFragment(pingsFragment, getString(R.string.ping_list_fragment_title));
        viewPager.setAdapter(adapter);
    }

    private class TraceratopsAppViewPagerFragmentAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> fragmentTitles = new ArrayList<>();

        public TraceratopsAppViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= 0 && position < fragments.size()) {
                return fragments.get(position);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position < fragmentTitles.size()) {
                return fragmentTitles.get(position);
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
