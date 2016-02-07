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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.ui.fragments.AddDebugPreferenceDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DebugItemAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final Map<String, ?> mKeyValuePairs;
    private final List<String> mKeys = new ArrayList<>();
    private Fragment mAttachedToFragment;

    private static final int TYPE_BOOLEAN = 1;
    private static final int TYPE_STRING = 2;
    private static final String EDIT_PREFERENCE_TAG = "com.bubblegum.traceratops.app.EditDebugPreference";

    public DebugItemAdapter(Fragment attachedToFragment, Map<String, ?> keyValuePairs) {
        mContext = attachedToFragment.getContext();
        mAttachedToFragment = attachedToFragment;
        mKeyValuePairs = keyValuePairs;
        mKeys.addAll(mKeyValuePairs.keySet());
    }

    private static class PreferenceViewHolder extends RecyclerView.ViewHolder {

        ViewGroup mStringGroup;
        ViewGroup mBooleanGroup;
        View itemView;

        public PreferenceViewHolder(View itemView) {
            super(itemView);
            mStringGroup = (ViewGroup) itemView.findViewById(R.id.row_item_string_group);
            mBooleanGroup = (ViewGroup) itemView.findViewById(R.id.row_item_boolean_group);
            this.itemView = itemView;
        }
    }

    private static class StringPreferenceViewHolder extends PreferenceViewHolder {

        TextView tvKeyName;
        TextView tvKeyValue;

        public StringPreferenceViewHolder(View itemView) {
            super(itemView);
            mBooleanGroup.setVisibility(View.GONE);
            mStringGroup.setVisibility(View.VISIBLE);
            tvKeyName = (TextView) mStringGroup.findViewById(R.id.row_item_string_key);
            tvKeyValue = (TextView) mStringGroup.findViewById(R.id.row_item_string_value);
        }

        public void fillInString(String key, String value) {
            tvKeyName.setText(key);
            tvKeyValue.setText(value);
        }
    }

    private static class BooleanPreferenceViewHolder extends PreferenceViewHolder {

        Switch swBoolean;

        public BooleanPreferenceViewHolder(View itemView) {
            super(itemView);
            mBooleanGroup.setVisibility(View.VISIBLE);
            mStringGroup.setVisibility(View.GONE);
            swBoolean = (Switch) mBooleanGroup.findViewById(R.id.row_item_boolean_switch);
        }

        public void fillInBoolean(String key, boolean value) {
            swBoolean.setText(key);
            swBoolean.setChecked(value);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_preference_entry, parent, false);
        switch (viewType) {
            case TYPE_BOOLEAN:
                return new BooleanPreferenceViewHolder(view);
            case TYPE_STRING:
                return new StringPreferenceViewHolder(view);
            default:
                return new PreferenceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final String key = mKeys.get(position);
        if(holder instanceof StringPreferenceViewHolder) {
            ((StringPreferenceViewHolder) holder).fillInString(key, (String) mKeyValuePairs.get(key));
            ((StringPreferenceViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddDebugPreferenceDialogFragment editDialog = new AddDebugPreferenceDialogFragment();
                    Bundle editArgs = new Bundle();
                    editArgs.putString(AddDebugPreferenceDialogFragment.DEBUG_KEY_NAME, key);
                    editArgs.putString(AddDebugPreferenceDialogFragment.DEBUG_VALUE, (String) mKeyValuePairs.get(key));
                    editArgs.putBoolean(AddDebugPreferenceDialogFragment.DEBUG_ARG_EDIT_MODE, true);
                    editDialog.setArguments(editArgs);
                    editDialog.show(mAttachedToFragment.getChildFragmentManager(), EDIT_PREFERENCE_TAG);
                }
            });
        } else if(holder instanceof BooleanPreferenceViewHolder) {
            ((BooleanPreferenceViewHolder) holder).fillInBoolean(key, (Boolean) mKeyValuePairs.get(key));
        }
    }

    @Override
    public int getItemCount() {
        return mKeys.size();
    }

    @Override
    public int getItemViewType(int position) {
        String key = mKeys.get(position);
        if(mKeyValuePairs.get(key) instanceof String) {
            return TYPE_STRING;
        }
        if(mKeyValuePairs.get(key) instanceof Boolean) {
            return TYPE_BOOLEAN;
        }
        return 0;
    }
}
