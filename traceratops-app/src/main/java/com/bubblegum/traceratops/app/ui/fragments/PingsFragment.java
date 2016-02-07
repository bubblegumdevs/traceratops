/*
 *
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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.profiles.AppProfile;
import com.bubblegum.traceratops.app.profiles.ProfileUpdateNotifier;
import com.bubblegum.traceratops.app.ui.adapters.plugins.PingEntryAdapter;

import java.util.List;

public class PingsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PingEntryAdapter mEntryAdapter;

    private ProfileUpdateNotifier mNotifier;
    private AppProfile mProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_pings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pings_recycler_view);
        AppProfile profile = TraceratopsApplication.from(getActivity()).getCurrentAppProfile();
        if(profile!=null) {
            setAdapterProfile(profile);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void setAdapterProfile(AppProfile profile) {
        mProfile = profile;
        mEntryAdapter = new PingEntryAdapter(getContext(), profile.getPingEntries());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mEntryAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mNotifier = new ProfileUpdateNotifier(TraceratopsApplication.from(getActivity()).getCurrentAppProfile()) {
            @Override
            protected void onProfileChanged(AppProfile newProfile, AppProfile oldProfile) {
                setAdapterProfile(newProfile);
            }

            @Override
            public void onEntryListUpdated(List<BaseEntry> mEntryList) {
                mEntryAdapter.notifyDataSetChanged();
                mEntryAdapter.updateDataSet(mProfile.getPingEntries());
            }

            @Override
            public void onEntryAdded(BaseEntry newEntry) {

            }

            @Override
            public void onEntriesCleared() {

            }
        };
        TraceratopsApplication.from(getActivity()).addProfileUpdateNotifier(mNotifier);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TraceratopsApplication.from(getActivity()).removeProfileUpdateNotifier(mNotifier);
    }
}
