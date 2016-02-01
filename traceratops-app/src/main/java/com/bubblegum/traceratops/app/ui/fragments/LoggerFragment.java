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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bubblegum.traceratops.app.LogStub;
import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.ui.activities.BaseActivity;
import com.bubblegum.traceratops.app.ui.adapters.BaseEntryAdapter;
import com.bubblegum.traceratops.app.ui.adapters.filters.LevelFilter;
import com.bubblegum.traceratops.app.ui.adapters.plugins.CrashAdapterPlugin;
import com.bubblegum.traceratops.app.ui.adapters.plugins.LogAdapterPlugin;
import com.bubblegum.traceratops.app.ui.adapters.plugins.PingAdapterPlugin;
import com.bubblegum.traceratops.app.ui.adapters.plugins.TLogAdapterPlugin;

import java.util.List;

public class LoggerFragment extends BaseFragment implements TraceratopsApplication.OnEntryListUpdatedListener {

    RecyclerView mRecyclerView;
    BaseEntryAdapter mEntryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_logger, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.logger_recycler_view);
        BaseActivity activity = (BaseActivity) getActivity();
        mEntryAdapter = new BaseEntryAdapter(activity, TraceratopsApplication.from(getActivity()).getEntries());
        mEntryAdapter.addAdapterPlugin(new LogAdapterPlugin(activity))
                .addAdapterPlugin(new TLogAdapterPlugin(activity))
                .addAdapterPlugin(new PingAdapterPlugin(activity))
                .addAdapterPlugin(new CrashAdapterPlugin(activity));
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mEntryAdapter);
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEntryAdapter.notifyDataSetChanged();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TraceratopsApplication.from(getActivity()).addOnEntryListUpdatedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TraceratopsApplication.from(getActivity()).removeOnEntryListUpdatedListener(this);
    }

    @Override
    public void onEntryListUpdated(List<BaseEntry> mEntryList) {
        mEntryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEntryAdded(BaseEntry newEntry) {

    }

    @Override
    public void onEntriesCleared() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_add_filter:
                addFilter();
                return true;

            case R.id.menu_log_clear_filter:
                clearFilters();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFilter() {
        TraceratopsApplication.from(getActivity()).addFilter(new LevelFilter(LogStub.WTF));
    }

    private void clearFilters() {
        TraceratopsApplication.from(getActivity()).clearFilters();
    }
}
