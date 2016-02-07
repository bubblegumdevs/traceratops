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

package com.bubblegum.traceratops.app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.profiles.AppProfile;
import com.bubblegum.traceratops.app.profiles.ProfileUpdateNotifier;
import com.bubblegum.traceratops.app.ui.adapters.DebugItemAdapter;

import java.util.List;

public class DebugFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ADD_PREFERENCE_TAG = "com.bubblegum.traceratops.app.AddDebugPreference";

    RecyclerView mRecyclerView;
    DebugItemAdapter mDebugItemAdapter;

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
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_debug, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.debugger_recycler_view);
        updateUI();
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateUI() {
        SharedPreferences preferences = getSharedPreferences();
        if(preferences!=null) {
            mDebugItemAdapter = new DebugItemAdapter(this, preferences.getAll());
            preferences.unregisterOnSharedPreferenceChangeListener(this);
            preferences.registerOnSharedPreferenceChangeListener(this);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(llm);
            mRecyclerView.setAdapter(mDebugItemAdapter);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_debug, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_debug_add:
                addPreference();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mDebugItemAdapter!=null) {
            mDebugItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TraceratopsApplication.from(getActivity()).addProfileUpdateNotifier(mNotifier);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TraceratopsApplication.from(getActivity()).removeProfileUpdateNotifier(mNotifier);
        SharedPreferences preferences = getSharedPreferences();
        if(preferences!=null) {
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    private void addPreference() {
        new AddDebugPreferenceDialogFragment().show(getChildFragmentManager(), ADD_PREFERENCE_TAG);
    }

    private SharedPreferences getSharedPreferences() {
        AppProfile profile = TraceratopsApplication.from(getActivity()).getCurrentAppProfile();
        if(profile==null) {
            return null;
        } else {
            return getActivity().getSharedPreferences(profile.targetPackageName, Context.MODE_PRIVATE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateUI();
    }
}
