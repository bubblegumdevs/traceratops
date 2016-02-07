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
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AppChooseSpinnerAdapter extends ArrayAdapter<ApplicationInfo> {

    List<ApplicationInfo> profiles;
    int resId;

    boolean isEmpty;


    public AppChooseSpinnerAdapter(Context context, int resource, List<ApplicationInfo> objects) {
        super(context, resource, objects);
        if(profiles==null || profiles.size() < 0) {
            isEmpty = true;
        }
        profiles = objects;
        resId = resource;
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView tvName = (TextView) view.findViewById(android.R.id.text1);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvName.setTextColor(Color.WHITE);
        tvName.setText(profiles.get(position).loadLabel(getContext().getPackageManager()));
//        tvName.setCompoundDrawablesWithIntrinsicBounds(profiles.get(position).loadIcon(getContext().getPackageManager()), null, null, null);
        tvName.setSingleLine(true);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tvName = (TextView) view.findViewById(android.R.id.text1);
        tvName.setText(profiles.get(position).loadLabel(getContext().getPackageManager()));
        tvName.setSingleLine(true);
        tvName.setCompoundDrawablesWithIntrinsicBounds(profiles.get(position).loadIcon(getContext().getPackageManager()), null, null, null);
        return view;
    }
}
