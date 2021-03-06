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

package com.bubblegum.traceratops.app.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.ui.activities.BaseActivity;
import com.bubblegum.traceratops.app.ui.adapters.plugins.AbsAdapterPlugin;
import com.bubblegum.traceratops.app.ui.adapters.plugins.GenericAdapterPlugin;

import java.util.ArrayList;
import java.util.List;

public class BaseEntryAdapter extends RecyclerView.Adapter {

    List<AbsAdapterPlugin<? extends BaseEntry>> mAdapterPlugins = new ArrayList<>();

    private final BaseActivity mBaseActivity;
    private final List<BaseEntry> mEntries;

    private int selectedIndex = -1;

    public BaseEntryAdapter(@NonNull BaseActivity baseActivity, @NonNull List<BaseEntry> entries) {
        mBaseActivity = baseActivity;
        mEntries = entries;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        public TextView vhPrimaryText;
        public TextView vhSecondaryText;
        public TextView vhTimestampText;
        public ImageView vhIcon;
        public View itemView;
        public View topSeparator;
        public View bottomSeparator;
        public TextView vhPrimaryAction;
        public TextView vhSecondaryAction;
        public ViewGroup vhButtonLayout;
        public int index;

        public EntryViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView.findViewById(R.id.item_view);
            vhPrimaryText = (TextView) itemView.findViewById(R.id.row_item_primary_text);
            vhSecondaryText = (TextView) itemView.findViewById(R.id.row_item_secondary_text);
            vhTimestampText = (TextView) itemView.findViewById(R.id.row_item_timestamp_text);
            vhPrimaryAction = (TextView) itemView.findViewById(R.id.primary_action);
            vhSecondaryAction = (TextView) itemView.findViewById(R.id.secondary_action);
            vhButtonLayout = (ViewGroup) itemView.findViewById(R.id.buttons);
            vhIcon = (ImageView) itemView.findViewById(R.id.row_item_icon);
            topSeparator = itemView.findViewById(R.id.top_separator);
            bottomSeparator = itemView.findViewById(R.id.bottom_separator);
        }
    }

    public BaseEntryAdapter addAdapterPlugin(@NonNull AbsAdapterPlugin<? extends BaseEntry> plugin) {
        mAdapterPlugins.add(plugin);
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.row_base_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EntryViewHolder) {
            ((EntryViewHolder) holder).index = position;
            BaseEntry entry = mEntries.get(position);
            AbsAdapterPlugin<? extends BaseEntry> plugin = findPluginForClass(entry.getClass());
            plugin.bind(entry, (EntryViewHolder) holder, this);
        }
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    private AbsAdapterPlugin<? extends BaseEntry> findPluginForClass(Class<? extends BaseEntry> clazz) {
        for(AbsAdapterPlugin<? extends BaseEntry> plugin : mAdapterPlugins) {
            if(clazz == plugin.getSupportedClass()) {
                return plugin;
            }
        }
        return new GenericAdapterPlugin(mBaseActivity);
    }
}
