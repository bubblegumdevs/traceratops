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

public abstract class BaseEntryFilter<T extends BaseEntry> {

    CharSequence constraint;

    public BaseEntryFilter(CharSequence constraint) {
        this.constraint = constraint;
    }

    @SuppressWarnings("unchecked") // handled case
    public boolean shouldFilterOut(BaseEntry entry) {
        if(getSupportedClass().isAssignableFrom(entry.getClass())) {
            return shouldFilterOut((T) entry, constraint);
        }
        return true;
    }

    protected abstract boolean shouldFilterOut(T entry, CharSequence constraint);

    protected abstract Class<T> getSupportedClass();
}
