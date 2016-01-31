/*
 *
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
import android.support.v4.content.ContextCompat;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.PingEntry;
import com.bubblegum.traceratops.app.ui.utils.CircularTextDrawable;

public class PingAdapterPlugin extends AbsAdapterPlugin<PingEntry> {
    @Override
    public Class<PingEntry> getSupportedClass() {
        return PingEntry.class;
    }

    @Override
    public String getPrimaryText(PingEntry entry) {
        if (entry.message != null) {
            /*
             * If a message is present then show the message regardless of the type
             */
            return entry.message;
        } else if (entry.timestampBegin == 0 && entry.timestampEnd == 0) {
            /* Both ping starting time and end time have not been recorded
             * This means that this ping is of type tick
             */
            return getContext().getString(R.string.ping_tick);
        } else if (entry.timestampEnd == 0) {
            /* Ping starting time has been recorded but not the end time
             * i.e. it's an unfinished ping
             */
            return getContext().getString(R.string.ping_start);
        } else {
            /* Both the start and end time of ping are defined
             * i.e. it's a finished ping
             */
            return getContext().getString(R.string.ping_end);
        }
    }

    @Override
    public String getSecondaryText(PingEntry entry) {
        /* Secondary text will be present only if message is not null
         * To inform the user if it's a finished or unfinished ping */
        if (entry.message != null) {
            if (entry.timestampBegin == 0 && entry.timestampEnd == 0) {
                // Tick
                if (entry.sizeInBytes != 0) {
                    return "Tick: " + entry.sizeInBytes + " bytes received";
                }
                return getContext().getString(R.string.ping_tick);
            } else if (entry.timestampEnd == 0) {
                // Unfinished ping
                return getContext().getString(R.string.ping_start);
            } else {
                // Finished ping
                return getContext().getString(R.string.ping_end);
            }
        } else {
            if (entry.sizeInBytes != 0) {
                return "" + entry.sizeInBytes + " bytes received";
            }
        }
        return null;
    }

    @Override
    public long getTimestamp(PingEntry entry) {
        return entry.timestamp;
    }

    @Override
    public Drawable getImageDrawable(PingEntry entry) {
        if (entry.timestampBegin == 0 && entry.timestampEnd == 0) {
            return new CircularTextDrawable("P", ContextCompat.getColor(getContext(), R.color.pingTick));
        }
        return new CircularTextDrawable("P", ContextCompat.getColor(getContext(), R.color.pingIndicator));
    }

    @Override
    public void onItemClick(PingEntry entry) {

    }
}
