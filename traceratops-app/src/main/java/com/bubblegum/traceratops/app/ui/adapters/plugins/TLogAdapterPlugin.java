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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.bubblegum.traceratops.app.LogStub;
import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.TLogEntry;
import com.bubblegum.traceratops.app.ui.activities.ObjectDetailsActivity;
import com.bubblegum.traceratops.app.ui.utils.CircularTextDrawable;

public class TLogAdapterPlugin extends AbsAdapterPlugin<TLogEntry> {

    @Override
    public Class<TLogEntry> getSupportedClass() {
        return TLogEntry.class;
    }

    @Override
    public String getPrimaryText(TLogEntry entry) {
        return entry.description;
    }

    @Override
    public String getSecondaryText(TLogEntry entry) {
        return entry.tag;
    }

    @Override
    public long getTimestamp(TLogEntry entry) {
        return entry.timestamp;
    }

    @Override
    public Drawable getImageDrawable(TLogEntry entry) {
        return new CircularTextDrawable(String.valueOf(entry.args.size()), LogStub.getColorForLevel(getContext(), entry.level));
    }

    @Override
    public void onItemClick(TLogEntry entry) {
        Intent intent = new Intent(getContext(), ObjectDetailsActivity.class);
        intent.putExtra(ObjectDetailsActivity.EXTRA_TLOG_OBJECT, entry.args);
        intent.putExtra(ObjectDetailsActivity.EXTRA_TLOG_TAG, entry.tag);
        intent.putExtra(ObjectDetailsActivity.EXTRA_TLOG_DESCRIPTION, entry.description);
        getContext().startActivity(intent);
    }
}
