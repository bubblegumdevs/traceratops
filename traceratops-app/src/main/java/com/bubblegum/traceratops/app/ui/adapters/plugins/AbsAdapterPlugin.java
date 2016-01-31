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
import android.support.v4.view.ViewCompat;
import android.text.format.DateUtils;
import android.view.View;

import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.ui.activities.BaseActivity;
import com.bubblegum.traceratops.app.ui.adapters.BaseEntryAdapter;

import java.lang.ref.WeakReference;

public abstract class AbsAdapterPlugin<T extends BaseEntry> {

    WeakReference<BaseActivity> activityRef;

    public abstract Class<T> getSupportedClass();

    public abstract String getPrimaryText(T entry);
    public abstract String getSecondaryText(T entry);
    public abstract long getTimestamp(T entry);
    public abstract Drawable getImageDrawable(T entry);

    public AbsAdapterPlugin(BaseActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @SuppressWarnings("unchecked") // We already check the class in BaseEntryAdapter
    public void bind(final BaseEntry entry, final BaseEntryAdapter.EntryViewHolder holder, final BaseEntryAdapter adapter) {
        if(entry.getClass() == getSupportedClass()) {
            boolean isThisItemSelected = holder.index == adapter.getSelectedIndex();
            holder.topSeparator.setVisibility(isThisItemSelected ? View.VISIBLE : View.GONE);
            holder.bottomSeparator.setVisibility(isThisItemSelected ? View.VISIBLE : View.GONE);
            holder.vhPrimaryText.setText(getPrimaryText((T) entry));
            holder.vhPrimaryText.setMaxLines(isThisItemSelected ? 5 : 2);
            ViewCompat.setTranslationZ(holder.itemView, isThisItemSelected ? 5 : 0);
            holder.vhSecondaryText.setText(getSecondaryText((T) entry));
            holder.vhTimestampText.setText(systemTimeInMillisToSystemDateFormat(getBaseActivity(), getTimestamp((T) entry)));
            holder.vhIcon.setImageDrawable(getImageDrawable((T) entry));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(adapter.getSelectedIndex()==holder.index) {
                        adapter.setSelectedIndex(-1);
                    } else {
                        adapter.setSelectedIndex(holder.index);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            boolean atLeastOneItemVisible = false;
            if(getPrimaryActionText()!=null) {
                atLeastOneItemVisible = true;
                holder.vhPrimaryAction.setVisibility(View.VISIBLE);
                holder.vhPrimaryAction.setText(getPrimaryActionText());
                holder.vhPrimaryAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPrimaryButtonClicked((T) entry);
                    }
                });
            } else {
                holder.vhPrimaryAction.setVisibility(View.GONE);
            }
            if(getSecondaryActionText()!=null) {
                atLeastOneItemVisible = true;
                holder.vhSecondaryAction.setVisibility(View.VISIBLE);
                holder.vhSecondaryAction.setText(getSecondaryActionText());
                holder.vhSecondaryAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSecondaryButtonClicked((T) entry);
                    }
                });
            } else {
                holder.vhSecondaryAction.setVisibility(View.GONE);
            }
            holder.vhButtonLayout.setVisibility(atLeastOneItemVisible && isThisItemSelected ? View.VISIBLE : View.GONE);
        }
    }

    protected abstract void onPrimaryButtonClicked(T entry);

    protected abstract void onSecondaryButtonClicked(T entry);

    protected abstract String getPrimaryActionText();

    protected abstract String getSecondaryActionText();

    protected BaseActivity getBaseActivity() {
        return activityRef.get();
    }

    private String systemTimeInMillisToSystemDateFormat(Context context, long millis) {
        return DateUtils.getRelativeDateTimeString(context, millis, DateUtils.SECOND_IN_MILLIS, 2 * DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }
}
