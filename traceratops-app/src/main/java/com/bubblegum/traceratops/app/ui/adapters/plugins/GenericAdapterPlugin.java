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

import android.graphics.drawable.Drawable;

import com.bubblegum.traceratops.app.model.BaseEntry;

public class GenericAdapterPlugin extends AbsAdapterPlugin<BaseEntry> {

    @Override
    public Class<BaseEntry> getSupportedClass() {
        return BaseEntry.class;
    }

    @Override
    public String getPrimaryText(BaseEntry entry) {
        return "(empty entry)";
    }

    @Override
    public String getSecondaryText(BaseEntry entry) {
        return "Traceratops";
    }

    @Override
    public long getTimestamp(BaseEntry entry) {
        return entry.timestamp;
    }

    @Override
    public Drawable getImageDrawable(BaseEntry entry) {
        return null;
    }
}
