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

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.ILoggerService;
import com.bubblegum.traceratops.sdk.client.annotations.TLogEntry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Log {

    static Log sInstance;
    boolean mShouldLog;
    private ILoggerService mLoggerService;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    LogProxy mLogProxy = LogProxies.DEFAULT_LOG_PROXY;

    Log() {

    }

    void setLoggerService(ILoggerService loggerService) {
        mLoggerService = loggerService;
    }
    
    private boolean dInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.d(tag, message)) {
            logInternalAsync(tag, message, null, LogProxy.D);
        }
        return true;
    }
    
    private boolean dInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.d(tag, message, throwable)) {
            logInternalAsync(tag, message, throwable, LogProxy.D);
        }
        return true;
    }
    
    private boolean eInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.e(tag, message)) {
            logInternalAsync(tag, message, null, LogProxy.E);
        }
        return true;
    }
    
    private boolean eInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.e(tag, message, throwable)) {
            logInternalAsync(tag, message, throwable, LogProxy.E);
        }
        return true;
    }

    private boolean iInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.e(tag, message)) {
            logInternalAsync(tag, message, null, LogProxy.I);
        }
        return true;
    }

    private boolean iInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.e(tag, message, throwable)) {
            logInternalAsync(tag, message, throwable, LogProxy.I);
        }
        return true;
    }
    
    private boolean vInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.v(tag, message)) {
            logInternalAsync(tag, message, null, LogProxy.V);
        }
        return true;
    }
    
    private boolean vInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.v(tag, message, throwable)) {
            logInternalAsync(tag, message, throwable, LogProxy.V);
        }
        return true;
    }
    
    private boolean wInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.w(tag, message)) {
            logInternalAsync(tag, message, null, LogProxy.W);
        }
        return true;
    }
    
    private boolean wInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.w(tag, message, throwable)) {
            logInternalAsync(tag, message, throwable, LogProxy.W);
        }
        return true;
    }

    private boolean wtfInternal(String tag, String message) {
        if(mLogProxy == null || mLogProxy.wtf(tag, message)) {
            logInternalAsync(tag, message, null, LogProxy.WTF);
        }
        return true;
    }
    
    private boolean wtfInternal(String tag, String message, Throwable throwable) {
        if(mLogProxy == null || mLogProxy.wtf(tag, message, throwable)) {
            logInternalAsync(tag, message, throwable, LogProxy.WTF);
        }
        return true;
    }

    public static void d(String tag, String message) {
        if(sInstance!=null) {
            sInstance.dInternal(tag, message);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void d(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.dInternal(tag, message, throwable);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void e(String tag, String message) {
        if(sInstance!=null) {
            sInstance.eInternal(tag, message);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.eInternal(tag, message, throwable);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void v(String tag, String message) {
        if(sInstance!=null) {
            sInstance.vInternal(tag, message);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void v(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.vInternal(tag, message, throwable);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void w(String tag, String message) {
        if(sInstance!=null) {
            sInstance.wInternal(tag, message);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void w(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.wInternal(tag, message, throwable);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void wtf(String tag, String message) {
        if(sInstance!=null) {
            sInstance.wtfInternal(tag, message);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        if(sInstance!=null) {
            sInstance.wtfInternal(tag, message, throwable);
        } else {
            Traceratops.warnNotLogging();
        }
    }

    static void crash(Throwable throwable) {
        sInstance.crashInternalAsync(throwable);
    }

    void logInternalAsync(final String tag, final String message, @Nullable final Object supplementObject, final int level) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                logInternal(tag, message, supplementObject, level);
            }
        });
    }

    private void logInternal(String tag, String message, @Nullable Object supplementObject, int level) {
        if(!isLogging()) {
            if(mShouldLog) {
                Traceratops.sInstance.attemptConnection();
            } else {
                return;
            }
        }
        try {
            if (mLoggerService != null) {
                if(supplementObject == null || supplementObject instanceof Throwable) {
                    String errorMessage = "";
                    if (supplementObject != null) {
                        errorMessage = getStackTraceAsString((Throwable) supplementObject);
                    }
                    mLoggerService.log(tag, message, errorMessage, level);
                } else {
                    Bundle inBundle = processForTLogEntries(supplementObject);
                    if(inBundle!=null) {
                        mLoggerService.tlog(tag, message, inBundle, level);
                    } else {
                        mLoggerService.log(tag, message, supplementObject.toString(), level);
                    }
                }
            }
        } catch (Throwable t) {
            if(Traceratops.sInstance.mLoggerServiceConnectionCallbacks !=null) {
                Traceratops.sInstance.mLoggerServiceConnectionCallbacks.onLoggerServiceException(t);
            }
        }
    }

    private void crashInternalAsync(final Throwable throwable) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                crashInternal(getStackTraceAsString(throwable), throwable.getMessage());
            }
        });
    }

    private void crashInternal(String stacktrace, String message) {
        try {
            if (mLoggerService != null) {
                mLoggerService.crash(stacktrace, message);
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
    
    private String getStackTraceAsString(@NonNull Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private Bundle processForTLogEntries(Object object) {
        Field []fields = object.getClass().getDeclaredFields();
        Bundle args = null;
        for(Field f : fields) {
            if(f.isAnnotationPresent(TLogEntry.class)) {
                try {
                    f.setAccessible(true);
                    String value = f.get(object).toString();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putString(f.getAnnotation(TLogEntry.class).value(), value);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return args;
    }
}
