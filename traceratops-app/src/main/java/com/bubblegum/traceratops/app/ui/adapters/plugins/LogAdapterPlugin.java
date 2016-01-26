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
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.LogEntry;
import com.bubblegum.traceratops.app.ui.utils.CircularTextDrawable;

public class LogAdapterPlugin extends AbsAdapterPlugin<LogEntry> {

    private static final int V = 2;
    private static final int D = 3;
    private static final int I = 4;
    private static final int W = 5;
    private static final int E = 6;
    private static final int WTF = 7;

    @Override
    public Class<LogEntry> getSupportedClass() {
        return LogEntry.class;
    }

    @Override
    public String getPrimaryText(LogEntry entry) {
        return entry.description;
    }

    @Override
    public String getSecondaryText(LogEntry entry) {
        return entry.tag;
    }

    @Override
    public long getTimestamp(LogEntry entry) {
        return entry.timestamp;
    }

    @Override
    public Drawable getImageDrawable(LogEntry entry) {
        return new CircularTextDrawable(getTextFromLevel(entry.level), getColorForLevel(entry.level));
    }

    @Override
    public void onItemClick(LogEntry entry) {

    }

    private String getTextFromLevel(int level) {
        switch (level) {
            case V:
                return "V";
            case D:
                return "D";
            case I:
                return "I";
            case E:
                return "E";
            case W:
                return "W";
            case WTF:
                return "F";
        }
        return "D";
    }

    private int getColorForLevel(int level) {
        switch (level) {
            case V:
                return getColor(R.color.logLevelVerbose);
            case D:
                return getColor(R.color.logLevelDebug);
            case I:
                return getColor(R.color.logLevelInfo);
            case E:
                return getColor(R.color.logLevelError);
            case W:
                return getColor(R.color.logLevelWarning);
            case WTF:
                return getColor(R.color.logLevelWTF);
        }
        return getColor(R.color.logLevelDebug);
    }

    private int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }
}
