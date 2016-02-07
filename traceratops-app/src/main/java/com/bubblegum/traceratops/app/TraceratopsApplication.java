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

package com.bubblegum.traceratops.app;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.app.profiles.AppProfile;
import com.bubblegum.traceratops.app.profiles.ProfileUpdateNotifier;
import com.bubblegum.traceratops.app.profiles.StandardAppProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceratopsApplication extends Application {

    Map<String, AppProfile> mConnectedAppProfiles = new HashMap<>();

    private Handler mMainThreadHandler;

    private List<ProfileUpdateNotifier> mProfileUpdateNotifierList = new ArrayList<>();

    public Collection<AppProfile> getProfiles() {
        return mConnectedAppProfiles.values();
    }

    public void addProfileUpdateNotifier(ProfileUpdateNotifier profileUpdateNotifier) {
        mProfileUpdateNotifierList.add(profileUpdateNotifier);
    }

    public void removeProfileUpdateNotifier(ProfileUpdateNotifier profileUpdateNotifier) {
        mProfileUpdateNotifierList.remove(profileUpdateNotifier);
    }

    private AppProfile currentAppProfile;

    public @Nullable AppProfile getCurrentAppProfile() {
        return currentAppProfile;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMainThreadHandler = new Handler(getMainLooper());
    }

    public static TraceratopsApplication from(Application application) {
        if(application instanceof TraceratopsApplication) {
            return (TraceratopsApplication) application;
        }
        return null;
    }

    public static TraceratopsApplication from(Activity activity) {
        if(activity!=null) {
            return from(activity.getApplication());
        }
        return null;
    }

    public static TraceratopsApplication from(Service service) {
        if(service!=null) {
            return from(service.getApplication());
        }
        return null;
    }

    public AppProfile makeAppProfile(IBinder mBinder, String packageName) {
        android.util.Log.d("TRACERT", "Make App Profile called for " + packageName);
        AppProfile profile = mConnectedAppProfiles.get(packageName);
        if(profile==null) {
            profile = new StandardAppProfile(mBinder);
            profile.targetPackageName = packageName;
            addProfile(profile);
            if(currentAppProfile==null) {
                setProfile(profile);
            }
        }
        return profile;
    }

    public AppProfile getProfile(String packageName) {
        return mConnectedAppProfiles.get(packageName);
    }

    public void setProfile(AppProfile profile) {
        currentAppProfile = profile;
        android.util.Log.d("TRACERT", "Setting profile to " + profile.targetPackageName);
        updateNotifiers(profile, false);
    }

    public void addProfile(AppProfile profile) {
        mConnectedAppProfiles.put(profile.targetPackageName, profile);
        updateNotifiers(profile, true);
    }

    private void updateNotifiers(final AppProfile profile, boolean addedNewProfile) {
        for(final ProfileUpdateNotifier notifier : mProfileUpdateNotifierList) {
            if(addedNewProfile) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        notifier.onNewProfileAdded(profile);
                    }
                });
            } else {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        notifier.setProfile(profile);
                    }
                });
            }
        }
    }

    private void runOnMainThread(Runnable runnable) {
        if(mMainThreadHandler!=null) {
            mMainThreadHandler.post(runnable);
        }
    }
}
