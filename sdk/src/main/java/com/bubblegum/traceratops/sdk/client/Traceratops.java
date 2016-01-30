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

package com.bubblegum.traceratops.sdk.client;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.ILoggerService;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Traceratops implements ServiceConnection {

    boolean mIsSafe = false;
    private static final String TAG = "bubblegum_traceratops";

    private boolean mHasWarnedNotLogging = false;

    ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private ILoggerService mLoggerService;
    LoggerServiceConnectionCallbacks mLoggerServiceConnectionCallbacks;

    private WeakReference<Context> mWeakContext;
    static Traceratops sInstance;

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
            Log.sInstance.setLoggerService(mLoggerService);
            TLog.sInstance = new TLog();
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

    private void setContextWeakly(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public void unbind() {
        if(mWeakContext!=null && mWeakContext.get()!=null) {
            mWeakContext.get().unbindService(this);
        }
    }

    void attemptConnection() {
        if(sInstance!=null && mWeakContext!=null && mWeakContext.get()!=null) {
            Intent binderIntent = new Intent("com.bubblegum.traceratops.BIND_LOGGER_SERVICE");
            binderIntent.setClassName("com.bubblegum.traceratops.app", "com.bubblegum.traceratops.app.service.LoggerService");
            mWeakContext.get().bindService(binderIntent, sInstance, Context.BIND_AUTO_CREATE);
        }
    }

    public static class Builder {

        private final Traceratops mInstance;
        private final Log mLogInstance;

        private Builder(Context context) {
            mInstance = new Traceratops();
            mInstance.setContextWeakly(context);
            mLogInstance = new Log();
        }

        public Builder withServiceConnectionCallbacks(@Nullable LoggerServiceConnectionCallbacks loggerServiceConnectionCallbacks) {
            mInstance.mLoggerServiceConnectionCallbacks = loggerServiceConnectionCallbacks;
            return this;
        }

        public Builder withLogProxy(@Nullable LogProxy logProxy) {
            mLogInstance.mLogProxy = logProxy;
            return this;
        }

        public Builder shouldLog(boolean shouldLog) {
            mLogInstance.setShouldLog(shouldLog);
            return this;
        }

        public Builder handleCrashes(final Application application) {
            final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();

            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    android.util.Log.e(TAG, "Traceratops has detected an uncaught exception B-) --> " + ex.getMessage().toString());
                    Log.crash(ex);

                    //Call the older uncaught exception handler
                    if (oldHandler != null) {
                        oldHandler.uncaughtException(thread, ex);
                    }
                }
            });
            return this;
        }

        public Traceratops connect() {
            sInstance = mInstance;
            Log.sInstance = mLogInstance;
            sInstance.attemptConnection();
            return sInstance;
        }
    }

    public static Traceratops.Builder setup(Context context) {
        return new Traceratops.Builder(context);
    }

    static void warnNotLogging() {
        // TODO Write something suitable for this
    }

    public interface LoggerServiceConnectionCallbacks {

        void onLoggerServiceConnected();
        void onLoggerServiceDisconnected();
        void onLoggerServiceException(Throwable t);

    }
}
