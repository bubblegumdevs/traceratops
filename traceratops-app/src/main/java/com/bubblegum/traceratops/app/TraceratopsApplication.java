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
import android.os.Handler;
import android.support.annotation.NonNull;

import com.bubblegum.traceratops.app.model.BaseEntry;

import java.util.ArrayList;
import java.util.List;

public class TraceratopsApplication extends Application {

    List<BaseEntry> mEntryList = new ArrayList<>();
    List<OnEntryListUpdatedListener> mOnEntryListUpdatedListeners = new ArrayList<>();

    private static final int ENTRY_ACTION_ADDED = 0;
    private static final int ENTRY_ACTION_CLEARED = 1;

    private Handler mMainThreadHandler;

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

    public void addOnEntryListUpdatedListener(OnEntryListUpdatedListener onEntryListUpdatedListener) {
        mOnEntryListUpdatedListeners.add(onEntryListUpdatedListener);
    }

    public void removeOnEntryListUpdatedListener(OnEntryListUpdatedListener onEntryListUpdatedListener) {
        mOnEntryListUpdatedListeners.remove(onEntryListUpdatedListener);
    }

    public static TraceratopsApplication from(Activity activity) {
        if(activity!=null) {
            return from(activity.getApplication());
        }
        return null;
    }

    public List<BaseEntry> getEntries() {
        return mEntryList;
    }

    public void addEntry(@NonNull BaseEntry entry) {
        mEntryList.add(0, entry);
        android.util.Log.d("TRACERT", "Log added");
        notifyListeners(entry, ENTRY_ACTION_ADDED);
    }

    public void notifyListeners(final BaseEntry entry, int action) {
        for(final OnEntryListUpdatedListener listener : mOnEntryListUpdatedListeners) {
            if(listener!=null) {
                listener.onEntryListUpdated(mEntryList);
                switch (action) {
                    case ENTRY_ACTION_ADDED:
                        runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onEntryAdded(entry);
                            }
                        });
                        break;

                    case ENTRY_ACTION_CLEARED:
                        runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onEntriesCleared();
                            }
                        });
                        break;
                }
            }
        }
    }

    private void runOnMainThread(Runnable runnable) {
        if(mMainThreadHandler!=null) {
            mMainThreadHandler.post(runnable);
        }
    }

    public void clearEntries() {
        mEntryList.clear();

    }

    public interface OnEntryListUpdatedListener {
        void onEntryListUpdated(List<BaseEntry> mEntryList);
        void onEntryAdded(BaseEntry newEntry);
        void onEntriesCleared();
    }

}
