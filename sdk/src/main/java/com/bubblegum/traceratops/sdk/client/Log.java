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

public final class Log implements ServiceConnection {

    private static Log sInstance;

    private LogProxy mLogProxy = LogProxies.DEFAULT_LOG_PROXY;
    
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
                attemptConnection();
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
            if(mLoggerServiceConnectionCallbacks !=null) {
                mLoggerServiceConnectionCallbacks.onLoggerServiceException(t);
            }
        }
    }

    private boolean mIsSafe = false;
    private boolean mShouldLog = true;

    private boolean mHasWarnedNotLogging = false;

    private ILoggerService mLoggerService;
    private LoggerServiceConnectionCallbacks mLoggerServiceConnectionCallbacks;

    private WeakReference<Context> mWeakContext;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLoggerService = ILoggerService.Stub.asInterface(service);
        if(mWeakContext!=null && mWeakContext.get()!=null) {
            mIsSafe = performSignatureCheck(mWeakContext.get(), name);
            if(!mIsSafe) {
                unbind();
                mLoggerService = null;
                if (mLoggerServiceConnectionCallbacks != null) {
                    mLoggerServiceConnectionCallbacks.onLoggerServiceException(new SecurityException("Signature check failed for Traceratops logger service. Please check if Traceratops app is signed with same key as this app."));
                }
                return;
            }
            Debug.sInstance = new Debug(mLoggerService);
        }
        if(mLoggerServiceConnectionCallbacks!=null) {
            mLoggerServiceConnectionCallbacks.onLoggerServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLoggerService = null;
        if(mLoggerServiceConnectionCallbacks!=null) {
            mLoggerServiceConnectionCallbacks.onLoggerServiceDisconnected();
        }
    }

    private boolean performSignatureCheck(Context context, ComponentName serverComponentName) {
        try {
            PackageManager pm = context.getPackageManager();
            Signature[] clientSigs = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            Signature[] serverSigs = pm.getPackageInfo(serverComponentName.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            for(int i =0; i < clientSigs.length; i++) {
                if (!clientSigs[i].toCharsString().equals(serverSigs[i].toCharsString())) {
                    return false;
                }
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public Log setShouldLog(boolean shouldLog) {
        mShouldLog = shouldLog;
        return this;
    }

    private boolean isLogging() {
        return mIsSafe && mShouldLog && mLoggerService!=null;
    }

    static void warnNotLogging() {
        // TODO Write something suitable for this
    }

    public interface LoggerServiceConnectionCallbacks {

        void onLoggerServiceConnected();
        void onLoggerServiceDisconnected();
        void onLoggerServiceException(Throwable t);

    }

    private void setContextWeakly(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    private Log() {

    }

    public static class Builder {

        private final Log mInstance;

        private Builder(Context context) {
            mInstance = new Log();
            mInstance.setContextWeakly(context);
        }

        public Builder withServiceConnectionCallbacks(@Nullable LoggerServiceConnectionCallbacks loggerServiceConnectionCallbacks) {
            mInstance.mLoggerServiceConnectionCallbacks = loggerServiceConnectionCallbacks;
            return this;
        }

        public Builder withLogProxy(@Nullable LogProxy logProxy) {
            mInstance.mLogProxy = logProxy;
            return this;
        }

        public Builder shouldLog(boolean shouldLog) {
            mInstance.setShouldLog(shouldLog);
            return this;
        }

        public Log connect() {
            sInstance = mInstance;
            sInstance.attemptConnection();
            return sInstance;
        }
    }

    public static Log.Builder setup(Context context) {
        return new Log.Builder(context);
    }

    private void attemptConnection() {
        if(sInstance!=null && mWeakContext!=null && mWeakContext.get()!=null) {
            Intent binderIntent = new Intent("com.bubblegum.traceratops.BIND_LOGGER_SERVICE");
            binderIntent.setClassName("com.bubblegum.traceratops.app", "com.bubblegum.traceratops.app.service.LoggerService");
            mWeakContext.get().bindService(binderIntent, sInstance, Context.BIND_AUTO_CREATE);
        }
    }

    private String getStackTraceAsString(@NonNull Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public void unbind() {
        if(mWeakContext!=null && mWeakContext.get()!=null) {
            mWeakContext.get().unbindService(this);
        }
    }
}
