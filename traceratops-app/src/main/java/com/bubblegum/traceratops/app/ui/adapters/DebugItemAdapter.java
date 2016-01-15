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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class DebugItemAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final Map<String, ?> mKeys;

    public DebugItemAdapter(Context context, Map<String, ?> keys) {
        mContext = context;
        mKeys = keys;
    }

    private abstract static class PreferenceViewHolder extends RecyclerView.ViewHolder {

        public PreferenceViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class StringPreferenceViewHolder extends PreferenceViewHolder {

        public StringPreferenceViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class BooleanPreferenceViewHolder extends PreferenceViewHolder {

        public BooleanPreferenceViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mKeys.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
