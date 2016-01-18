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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubblegum.traceratops.app.R;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetailAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final Bundle args;
    private final List<String> mKeys = new ArrayList<>();

    public ObjectDetailAdapter(Context context, Bundle args) {
        mContext = context;
        this.args = args;
        mKeys.addAll(args.keySet());
    }

    private class ObjectDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView heading;
        private TextView body;

        public ObjectDetailsViewHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.row_heading);
            body = (TextView) itemView.findViewById(R.id.row_body);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_object_item, parent, false);
        return new ObjectDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String key = mKeys.get(position);
        String value = args.getString(key);
        if(holder instanceof ObjectDetailsViewHolder) {
            ((ObjectDetailsViewHolder) holder).body.setText(value);
            ((ObjectDetailsViewHolder) holder).heading.setText(key);
        }
    }

    @Override
    public int getItemCount() {
        return mKeys.size();
    }
}
