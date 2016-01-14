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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.ui.adapters.plugins.AbsAdapterPlugin;
import com.bubblegum.traceratops.app.ui.adapters.plugins.GenericAdapterPlugin;

import java.util.ArrayList;
import java.util.List;

public class BaseEntryAdapter extends RecyclerView.Adapter {

    List<AbsAdapterPlugin<? extends BaseEntry>> mAdapterPlugins = new ArrayList<>();

    private final Context mContext;
    private final List<BaseEntry> mEntries;

    public BaseEntryAdapter(@NonNull Context context, @NonNull List<BaseEntry> entries) {
        mContext = context;
        mEntries = entries;
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        public TextView vhPrimaryText;
        public TextView vhSecondaryText;
        public TextView vhTimestampText;
        public ImageView vhIcon;

        public EntryViewHolder(View itemView) {
            super(itemView);
            vhPrimaryText = (TextView) itemView.findViewById(R.id.row_item_primary_text);
            vhSecondaryText = (TextView) itemView.findViewById(R.id.row_item_secondary_text);
            vhTimestampText = (TextView) itemView.findViewById(R.id.row_item_timestamp_text);
            vhIcon = (ImageView) itemView.findViewById(R.id.row_item_icon);
        }
    }

    public BaseEntryAdapter addAdapterPlugin(@NonNull AbsAdapterPlugin<? extends BaseEntry> plugin) {
        mAdapterPlugins.add(plugin);
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_base_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EntryViewHolder) {
            BaseEntry entry = mEntries.get(position);
            AbsAdapterPlugin<? extends BaseEntry> plugin = findPluginForClass(entry.getClass());
            plugin.bind(mContext, entry, (EntryViewHolder) holder);
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
        return new GenericAdapterPlugin();
    }
}
