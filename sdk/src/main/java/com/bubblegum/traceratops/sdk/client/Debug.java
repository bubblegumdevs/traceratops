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

package com.bubblegum.traceratops.sdk.client;

import android.os.RemoteException;

import com.bubblegum.traceratops.ILoggerService;

public class Debug {

    static Debug sInstance;
    private final ILoggerService mLoggerService;

    Debug(ILoggerService loggerService) {
        mLoggerService = loggerService;
    }

    private String getStringInternal(String key, String defaultValue) {
        try {
            if (sInstance.mLoggerService != null) {
                return sInstance.mLoggerService.getString(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (RemoteException e) {
            return defaultValue;
        }
    }

    private boolean getBooleanInternal(String key, boolean defaultValue) {
        try {
            if (sInstance.mLoggerService != null) {
                return sInstance.mLoggerService.getBoolean(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (RemoteException e) {
            return defaultValue;
        }
    }

    public static String getString(String key, String defaultValue) {
        if (sInstance == null) {
            Traceratops.warnNotLogging();
            return defaultValue;
        } else {
            return sInstance.getStringInternal(key, defaultValue);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (sInstance == null) {
            Traceratops.warnNotLogging();
            return defaultValue;
        } else {
            return sInstance.getBooleanInternal(key, defaultValue);
        }
    }

}
