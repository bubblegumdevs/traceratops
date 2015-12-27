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

public final class Log implements LogProxy, ServiceConnection {

    private static Log sInstance;

    private boolean mIsSafe = false;
    private boolean mShouldLog = true;

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

    public void setShouldLog(boolean shouldLog) {
        mShouldLog = shouldLog;
    }

    private boolean isLogging() {
        return mIsSafe && mShouldLog && mLoggerService!=null;
    }

    public interface LoggerServiceConnectionCallbacks {

        void onLoggerServiceConnected();
        void onLoggerServiceDisconnected();
        void onLoggerServiceException(Throwable t);

    }

    public void setContextWeakly(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public static Log getInstance(@NonNull Context context, @Nullable LoggerServiceConnectionCallbacks loggerServiceConnectionCallbacks) {
        if(sInstance == null) {
            sInstance = new Log(loggerServiceConnectionCallbacks);
            sInstance.setContextWeakly(context);
            sInstance.attemptConnection();
        }
        return sInstance;
    }

    private Log(@Nullable LoggerServiceConnectionCallbacks loggerServiceConnectionCallbacks) {
        mLoggerServiceConnectionCallbacks = loggerServiceConnectionCallbacks;
    }

    private void attemptConnection() {
        if(mWeakContext!=null && mWeakContext.get()!=null) {
            Intent binderIntent = new Intent("com.bubblegum.traceratops.BIND_LOGGER_SERVICE");
            binderIntent.setClassName("com.bubblegum.traceratops.app", "com.bubblegum.traceratops.app.service.LoggerService");
            mWeakContext.get().bindService(binderIntent, sInstance, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void d(String tag, String message) {
        android.util.Log.d(tag, message);
        logInternal(tag, message, null, LogProxy.D);
    }

    @Override
    public void d(String tag, String message, Throwable throwable) {
        android.util.Log.d(tag, message, throwable);
        logInternal(tag, message, throwable, LogProxy.D);
    }

    @Override
    public void e(String tag, String message) {
        android.util.Log.e(tag, message);
        logInternal(tag, message, null, LogProxy.E);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        android.util.Log.e(tag, message, throwable);
        logInternal(tag, message, throwable, LogProxy.E);
    }

    @Override
    public void v(String tag, String message) {
        android.util.Log.v(tag, message);
        logInternal(tag, message, null, LogProxy.V);
    }

    @Override
    public void v(String tag, String message, Throwable throwable) {
        android.util.Log.v(tag, message, throwable);
        logInternal(tag, message, throwable, LogProxy.V);
    }

    @Override
    public void w(String tag, String message) {
        android.util.Log.w(tag, message);
        logInternal(tag, message, null, LogProxy.W);
    }

    @Override
    public void w(String tag, String message, Throwable throwable) {
        android.util.Log.w(tag, message, throwable);
        logInternal(tag, message, throwable, LogProxy.W);
    }

    @Override
    public void wtf(String tag, String message) {
        android.util.Log.wtf(tag, message);
        logInternal(tag, message, null, LogProxy.WTF);
    }

    @Override
    public void wtf(String tag, String message, Throwable throwable) {
        android.util.Log.wtf(tag, message, throwable);
        logInternal(tag, message, throwable, LogProxy.WTF);
    }

    private void logInternal(String tag, String message, @Nullable Throwable throwable, String level) {
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
