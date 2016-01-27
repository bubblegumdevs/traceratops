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

package com.bubblegum.traceratops.app;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class LogStub {
    public static final int V = 2;
    public static final int D = 3;
    public static final int I = 4;
    public static final int W = 5;
    public static final int E = 6;
    public static final int WTF = 7;

    public static String getTextFromLevel(int level) {
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

    public static int getColorForLevel(Context context, int level) {
        switch (level) {
            case V:
                return getColor(context, R.color.logLevelVerbose);
            case D:
                return getColor(context, R.color.logLevelDebug);
            case I:
                return getColor(context, R.color.logLevelInfo);
            case E:
                return getColor(context, R.color.logLevelError);
            case W:
                return getColor(context, R.color.logLevelWarning);
            case WTF:
                return getColor(context, R.color.logLevelWTF);
        }
        return getColor(context, R.color.logLevelDebug);
    }

    private static int getColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }
}
