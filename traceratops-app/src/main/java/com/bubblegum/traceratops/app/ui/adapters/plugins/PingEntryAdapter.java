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

package com.bubblegum.traceratops.app.ui.adapters.plugins;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.PingEntry;
import com.bubblegum.traceratops.app.ui.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PingEntryAdapter extends RecyclerView.Adapter {

    private Map<Integer, List<PingEntry>> mPingEntriesMap;
    private List<Integer> mPingTokens;
    private Context mContext;

    public PingEntryAdapter(@NonNull Context context, @NonNull Map<Integer, List<PingEntry>> pingEntriesMap) {
        mPingEntriesMap = pingEntriesMap;
        mPingTokens = new ArrayList<>();
        if (pingEntriesMap != null) {
            mPingTokens.addAll(pingEntriesMap.keySet());
        }
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_ping_entry, parent, false);
        return new PingEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        List<PingEntry> entries = mPingEntriesMap.get(mPingTokens.get(position));

        // Reading all ping parameters
        String message = "[PING]";
        long startTime = 0;
        long endTime = 0;
        long timestamp = entries.get(0).timestamp;
        long numTicks = 0;
        boolean unfinished = true;
        for (PingEntry entry : entries) {
            if (!TextUtils.isEmpty(entry.message)) {
                message = entry.message;
            }
            if (entry.timestampBegin != 0) {
                startTime = entry.timestampBegin;
            }

            if (entry.timestampEnd != 0) {
                endTime = entry.timestampEnd;
                unfinished = false;
            }

            if (entry.timestampEnd == 0 && entry.timestampBegin == 0) {
                numTicks++;
            }
        }
        double duration = (endTime != 0) ? (endTime-startTime)/1000.0 : 0;

        // Updating views
        PingEntryViewHolder pingViewHolder = (PingEntryViewHolder) holder;
        pingViewHolder.index = position;
        pingViewHolder.primaryMessageTv.setText(message);
        if (unfinished) {
            pingViewHolder.durationTv.setText(R.string.error_unfinished_ping);
            pingViewHolder.durationTv.setTextColor(Color.RED);
        } else {
            pingViewHolder.durationTv.setText("Duration: " + duration + " seconds");
            pingViewHolder.durationTv.setTextColor(Color.DKGRAY);
        }
        pingViewHolder.timestampTv.setText(UIUtils.systemTimeInMillisToSystemDateFormat(mContext, timestamp));
        pingViewHolder.ticksTv.setText(numTicks == 0 ? "" : ""+numTicks+" ticks");
    }

    @Override
    public int getItemCount() {
        return mPingTokens.size();
    }

    public void updateDataSet(Map<Integer, List<PingEntry>> pingEntriesMap) {
        mPingEntriesMap = pingEntriesMap;
        if (pingEntriesMap != null) {
            mPingTokens.addAll(pingEntriesMap.keySet());
        }
        notifyDataSetChanged();
    }

    private static class PingEntryViewHolder extends RecyclerView.ViewHolder {

        public TextView primaryMessageTv;
        public TextView durationTv;
        public TextView ticksTv;
        public TextView timestampTv;
        public int index;

        public PingEntryViewHolder(View itemView) {
            super(itemView);
            primaryMessageTv = (TextView) itemView.findViewById(R.id.ping_message_tv);
            durationTv = (TextView) itemView.findViewById(R.id.duration_tv);
            ticksTv = (TextView) itemView.findViewById(R.id.ticks_tv);
            timestampTv = (TextView) itemView.findViewById(R.id.timestamp_tv);
        }
    }
}
