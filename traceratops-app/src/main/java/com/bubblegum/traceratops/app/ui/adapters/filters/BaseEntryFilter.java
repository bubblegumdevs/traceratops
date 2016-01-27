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

package com.bubblegum.traceratops.app.ui.adapters.filters;

import android.widget.Filter;

import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.ui.adapters.BaseEntryAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntryFilter<T extends BaseEntry> extends Filter {

    private final BaseEntryAdapter mBaseEntryAdapter;

    public BaseEntryFilter(BaseEntryAdapter adapter) {
        mBaseEntryAdapter = adapter;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        final List<BaseEntry> preFilter = mBaseEntryAdapter.getFilteredEntries();
        final List<BaseEntry> postFilter = new ArrayList<>();
        for(BaseEntry entry : preFilter) {
            if(entry.getClass() == getSupportedClass()) {
                if (!shouldFilterOut((T) entry, constraint)) {
                    postFilter.add(entry);
                }
            }
        }
        results.values = postFilter;
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mBaseEntryAdapter.getFilteredEntries().clear();
        mBaseEntryAdapter.getFilteredEntries().addAll((List<BaseEntry>)results.values);
        mBaseEntryAdapter.notifyDataSetChanged();
    }

    protected abstract boolean shouldFilterOut(T entry, CharSequence constraint);

    protected abstract Class<T> getSupportedClass();
}
