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

package com.bubblegum.traceratops.app.profiles;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.model.PingEntry;
import com.bubblegum.traceratops.app.ui.adapters.filters.BaseEntryFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AppProfile {

    public interface OnEntryListUpdatedListener {
        void onEntryListUpdated(List<BaseEntry> mEntryList);
        void onEntryAdded(BaseEntry newEntry);
        void onEntriesCleared();
    }

    List<BaseEntry> mEntryList = new ArrayList<>();
    List<BaseEntry> mFilteredEntryList = new ArrayList<>();
    List<OnEntryListUpdatedListener> mOnEntryListUpdatedListeners = new ArrayList<>();
    Map<Integer, List<PingEntry>> mPingEntryMap = new HashMap<>();

    public String targetPackageName;

    List<BaseEntryFilter> filters = new ArrayList<>();

    private int errorCode = -1;

    private static final int ENTRY_ACTION_ADDED = 1;
    private static final int ENTRY_ACTION_CLEARED = 2;

    private final Handler mMainThreadHandler;

    public AppProfile(IBinder binder) {
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void notifyListeners(final BaseEntry entry, int action) {
        for(final OnEntryListUpdatedListener listener : mOnEntryListUpdatedListeners) {
            if(listener!=null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onEntryListUpdated(mEntryList);
                    }
                });
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
        mFilteredEntryList.clear();
    }

    public void addFilter(BaseEntryFilter filter) {
        filters.add(filter);
        List<BaseEntry> entriesToBeRemoved = new ArrayList<>();
        for(int i = 0; i < mFilteredEntryList.size(); i++) {
            BaseEntry entry = mFilteredEntryList.get(i);
            if(filter.shouldFilterOut(entry)) {
                entriesToBeRemoved.add(0, entry);
            }
        }
        for(BaseEntry entry : entriesToBeRemoved) {
            mFilteredEntryList.remove(entry);
        }
        notifyListeners(null, 0);
    }

    public void clearFilters() {
        filters.clear();
        mFilteredEntryList.clear();
        mFilteredEntryList.addAll(mEntryList);
        notifyListeners(null, 0);
    }

    public List<BaseEntryFilter> getCurrentFilters() {
        return filters;
    }

    public void setErrorCode(int errorCode) {
        android.util.Log.d("TRACERT", "Error code set to " + errorCode + " for " + targetPackageName);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<BaseEntry> getEntries() {
        return mFilteredEntryList;
    }

    public Map<Integer, List<PingEntry>> getPingEntries() {
        return mPingEntryMap;
    }

    public void addEntry(@NonNull BaseEntry entry) {
        if (entry.getClass().equals(PingEntry.class)) {
            addPingEntry((PingEntry) entry);
        }

        mEntryList.add(0, entry);
        boolean shouldFilterOut = false;
        for(BaseEntryFilter filter : filters) {
            shouldFilterOut = filter.shouldFilterOut(entry);
            if(shouldFilterOut) {
                break;
            }
        }
        if(!shouldFilterOut) {
            mFilteredEntryList.add(0, entry);
        }
        android.util.Log.d("TRACERT", "Log added");
        notifyListeners(entry, ENTRY_ACTION_ADDED);
    }

    private void addPingEntry(PingEntry pingEntry) {
        int pingId = pingEntry.token;
        if (mPingEntryMap.containsKey(pingId)) {
            List<PingEntry> existingPingEntries = mPingEntryMap.get(pingId);
            existingPingEntries.add(pingEntry);
            mPingEntryMap.put(pingId, existingPingEntries);
        } else {
            List<PingEntry> newPingEntry = new ArrayList<>();
            newPingEntry.add(pingEntry);
            mPingEntryMap.put(pingId, newPingEntry);
        }
    }

    public void addOnEntryListUpdatedListener(OnEntryListUpdatedListener onEntryListUpdatedListener) {
        mOnEntryListUpdatedListeners.add(onEntryListUpdatedListener);
    }

    public void removeOnEntryListUpdatedListener(OnEntryListUpdatedListener onEntryListUpdatedListener) {
        mOnEntryListUpdatedListeners.remove(onEntryListUpdatedListener);
    }
}
