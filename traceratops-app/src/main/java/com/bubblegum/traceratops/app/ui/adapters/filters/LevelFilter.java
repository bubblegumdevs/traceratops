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

import com.bubblegum.traceratops.app.model.BaseEntry;
import com.bubblegum.traceratops.app.model.LogEntry;
import com.bubblegum.traceratops.app.model.TLogEntry;
import com.bubblegum.traceratops.app.ui.adapters.BaseEntryAdapter;

public class LevelFilter extends BaseEntryFilter<BaseEntry> {

    public LevelFilter(int constraint) {
        super(String.valueOf(constraint));
    }

    @Override
    protected boolean shouldFilterOut(BaseEntry entry, CharSequence constraint) {
        if(entry instanceof LogEntry) {
            return ((LogEntry)entry).level < Integer.parseInt(constraint.toString());
        }
        return true;
    }

    @Override
    protected Class<BaseEntry> getSupportedClass() {
        return BaseEntry.class;
    }
}
