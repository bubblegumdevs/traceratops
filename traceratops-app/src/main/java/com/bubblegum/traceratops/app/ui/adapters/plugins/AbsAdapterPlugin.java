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

package com.bubblegum.traceratops.app.ui.adapters.plugins;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.View;

import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.ui.adapters.BaseEntryAdapter;

public abstract class AbsAdapterPlugin<T extends BaseEntry> {

    Context context;

    public abstract Class<T> getSupportedClass();

    public abstract String getPrimaryText(T entry);
    public abstract String getSecondaryText(T entry);
    public abstract long getTimestamp(T entry);
    public abstract Drawable getImageDrawable(T entry);
    public abstract void onItemClick(T entry);

    @SuppressWarnings("unchecked") // We already check the class in BaseEntryAdapter
    public void bind(Context context, final BaseEntry entry, BaseEntryAdapter.EntryViewHolder holder) {
        this.context = context;
        if(entry.getClass() == getSupportedClass()) {
            holder.vhPrimaryText.setText(getPrimaryText((T) entry));
            holder.vhSecondaryText.setText(getSecondaryText((T) entry));
            holder.vhTimestampText.setText(systemTimeInMillisToSystemDateFormat(context, getTimestamp((T) entry)));
            holder.vhIcon.setImageDrawable(getImageDrawable((T) entry));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick((T) entry);
                }
            });
        }
    }

    protected Context getContext() {
        return context;
    }

    private String systemTimeInMillisToSystemDateFormat(Context context, long millis) {
        return DateUtils.getRelativeDateTimeString(context, millis, DateUtils.SECOND_IN_MILLIS, 2 * DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }
}
