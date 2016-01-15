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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.ILoggerService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

public final class Log {

    static Log sInstance;
    boolean mShouldLog;
    private ILoggerService mLoggerService;

    LogProxy mLogProxy = LogProxies.DEFAULT_LOG_PROXY;

    Log() {

    }

    void setLoggerService(ILoggerService loggerService) {
        mLoggerService = loggerService;
    }
    
    private boolean dInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.d(tag, message)) {
            logInternal(tag, message, null, LogProxy.D);
        }
        return true;
    }
    
    private boolean dInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.d(tag, message, throwable)) {
            logInternal(tag, message, throwable, LogProxy.D);
        }
        return true;
    }
    
    private boolean eInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.e(tag, message)) {
            logInternal(tag, message, null, LogProxy.E);
        }
        return true;
    }
    
    private boolean eInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.e(tag, message, throwable)) {
            logInternal(tag, message, throwable, LogProxy.E);
        }
        return true;
    }

    private boolean iInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.e(tag, message)) {
            logInternal(tag, message, null, LogProxy.I);
        }
        return true;
    }

    private boolean iInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.e(tag, message, throwable)) {
            logInternal(tag, message, throwable, LogProxy.I);
        }
        return true;
    }
    
    private boolean vInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.v(tag, message)) {
            logInternal(tag, message, null, LogProxy.V);
        }
        return true;
    }
    
    private boolean vInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.v(tag, message, throwable)) {
            logInternal(tag, message, throwable, LogProxy.V);
        }
        return true;
    }
    
    private boolean wInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.w(tag, message)) {
            logInternal(tag, message, null, LogProxy.W);
        }
        return true;
    }
    
    private boolean wInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.w(tag, message, throwable)) {
            logInternal(tag, message, throwable, LogProxy.W);
        }
        return true;
    }

    private boolean wtfInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.wtf(tag, message)) {
            logInternal(tag, message, null, LogProxy.WTF);
        }
        return true;
    }
    
    private boolean wtfInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.wtf(tag, message, throwable)) {
            logInternal(tag, message, throwable, LogProxy.WTF);
        }
        return true;
    }

    public static void d(String tag, String message) {
        if(sInstance!=null) {
            sInstance.dInternal(tag, message);
        } else {
            warnNotLogging();
        }
    }

    public static void d(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.dInternal(tag, message, throwable);
        } else {
            warnNotLogging();
        }
    }

    public static void e(String tag, String message) {
        if(sInstance!=null) {
            sInstance.eInternal(tag, message);
        } else {
            warnNotLogging();
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.eInternal(tag, message, throwable);
        } else {
            warnNotLogging();
        }
    }

    public static void v(String tag, String message) {
        if(sInstance!=null) {
            sInstance.vInternal(tag, message);
        } else {
            warnNotLogging();
        }
    }

    public static void v(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.vInternal(tag, message, throwable);
        } else {
            warnNotLogging();
        }
    }

    public static void w(String tag, String message) {
        if(sInstance!=null) {
            sInstance.wInternal(tag, message);
        } else {
            warnNotLogging();
        }
    }

    public static void w(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.wInternal(tag, message, throwable);
        } else {
            warnNotLogging();
        }
    }

    public static void wtf(String tag, String message) {
        if(sInstance!=null) {
            sInstance.wtfInternal(tag, message);
        } else {
            warnNotLogging();
        }
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.wtfInternal(tag, message, throwable);
        } else {
            warnNotLogging();
        }
    }

    private void logInternal(String tag, String message, @Nullable Throwable throwable, int level) {
        if(!isLogging()) {
            if(mShouldLog) {
                Traceratops.sInstance.attemptConnection();
            } else {
                return;
            }
        }
        try {
            if (mLoggerService != null) {
                String errorMessage = "";
                if(throwable!=null) {
                    errorMessage = getStackTraceAsString(throwable);
                }
                mLoggerService.log(tag, message, errorMessage, level);
            }
        } catch (Throwable t) {
            if(Traceratops.sInstance.mLoggerServiceConnectionCallbacks !=null) {
                Traceratops.sInstance.mLoggerServiceConnectionCallbacks.onLoggerServiceException(t);
            }
        }
    }

    public Log setShouldLog(boolean shouldLog) {
        mShouldLog = shouldLog;
        return this;
    }

    private boolean isLogging() {
        return Traceratops.sInstance.mIsSafe && mShouldLog && mLoggerService!=null;
    }

    static void warnNotLogging() {
        // TODO Write something suitable for this
    }

    private String getStackTraceAsString(@NonNull Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
