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
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.ILoggerService;
import com.bubblegum.traceratops.sdk.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Traceratops implements ServiceConnection {

    boolean mIsSafe = false;
    boolean mAppOutdated = false;
    boolean mSDKOutdated = false;
    boolean mShouldLog;
    boolean mIsSameSignature = false;
    boolean mTrustAgentInstalled = false;

    private static final String TAG = "bubblegum_traceratops";

    private int mTrustMode = TrustMode.TRUST_MODE_CERTIFICATE_CHECK_ONLY;

    static final int MIN_APP_VERSION = 1;

    private boolean mHasWarnedNotLogging = false;

    ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private ILoggerService mLoggerService;
    LoggerServiceConnectionCallbacks mLoggerServiceConnectionCallbacks;

    private WeakReference<Context> mWeakContext;
    static Traceratops sInstance;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        String packageName;
        if(mWeakContext!=null && mWeakContext.get()!=null) {
            packageName = mWeakContext.get().getPackageName();
        } else {
            return;
        }
        mLoggerService = ILoggerService.Stub.asInterface(service);
        try {
            mLoggerService.reportPackage(packageName);
        } catch (RemoteException e) {
        }
        mAppOutdated = false;
        mSDKOutdated = false;
        try {
            int appVersion = mLoggerService.checkVersion(BuildConfig.VERSION_CODE);
            if (appVersion < 0) {
                mSDKOutdated = true;
            } else if (appVersion > 0 && appVersion < MIN_APP_VERSION) {
                mAppOutdated = true;
            }
        } catch (RemoteException ignored) {
        }
        if (!isCompatible()) {
            reportError();
            unbind();
            mLoggerService = null;
            if (mLoggerServiceConnectionCallbacks != null) {
                mLoggerServiceConnectionCallbacks.onLoggerServiceException(new IllegalArgumentException(mAppOutdated ? "Please install the latest version of Traceratops." : "This version of Traceratops needs a newer SDK. Please update the SDK."));
            }
            return;
        }
        if (mWeakContext != null && mWeakContext.get() != null) {
            String trustAgentPackage = getTrustedAgentPackageName(mWeakContext.get());
            String trustPermission = getTrustPermission(mWeakContext.get());
            mIsSafe = performSignatureCheck(mWeakContext.get(), trustAgentPackage, trustPermission);
            if (!mIsSafe) {
                reportError();
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
            Ping.sInstance = new Ping(mLoggerService);
        }
        if (mLoggerServiceConnectionCallbacks != null) {
            mLoggerServiceConnectionCallbacks.onLoggerServiceConnected();
        }
    }

    private String getTrustedAgentPackageName(Context context) {
        return context.getPackageName().concat(".trust");
    }

    private String getTrustPermission(Context context) {
        return context.getPackageName().concat(".TRUST");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLoggerService = null;
        if (mLoggerServiceConnectionCallbacks != null) {
            mLoggerServiceConnectionCallbacks.onLoggerServiceDisconnected();
        }
    }

    private boolean performSignatureCheck(Context context, String trustAgentPackageName, String trustPermission) {
        if (mTrustMode == TrustMode.TRUST_MODE_OVERRIDE) {
            return true;
        }
        try {
            PackageManager pm = context.getPackageManager();
            if (!isPackagePresent(pm, trustAgentPackageName)) {
                return false;
            }
            mTrustAgentInstalled = true;
            boolean foundMismatch = false;
            Signature[] clientSigs = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            Signature[] serverSigs = pm.getPackageInfo(trustAgentPackageName, PackageManager.GET_SIGNATURES).signatures;
            for (int i = 0; i < clientSigs.length; i++) {
                if (!clientSigs[i].toCharsString().equals(serverSigs[i].toCharsString())) {
                    foundMismatch = true;
                }
            }
            mIsSameSignature = !foundMismatch;
            if (mTrustMode == TrustMode.TRUST_MODE_CERTIFICATE_CHECK_ONLY || !mIsSameSignature) {
                return mIsSameSignature;
            }
            boolean sigCheck = false;
            try {
                Cursor cursor = context.getContentResolver().query(Uri.parse("content://" + trustPermission), null, null, null, null);
                if (cursor != null) {
                    sigCheck = true;
                    cursor.close();
                }
            } catch (Throwable t) {
                sigCheck = false;
            }
            return sigCheck;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isPackagePresent(PackageManager pm, String packageName) {
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void reportError() {
        try {
            int errorCode = 0;
            if (!mTrustAgentInstalled) {
                errorCode = ERROR_CODES.ERROR_CODE_TRUST_AGENT_MISSING;
            } else if (!mIsSafe) {
                errorCode = mIsSameSignature ? ERROR_CODES.ERROR_CODE_SIGNATURE_VERIFICATION_FAILED_MIGHT_NEED_ACTIVATION : ERROR_CODES.ERROR_CODE_SIGNATURE_VERIFICATION_FAILED;
            } else if (mAppOutdated) {
                errorCode = ERROR_CODES.ERROR_CODE_APP_OUTDATED;
            } else if (mSDKOutdated) {
                errorCode = ERROR_CODES.ERROR_CODE_SDK_OUTDATED;
            }
            if (mLoggerService != null) {
                mLoggerService.reportError(errorCode);
            }
        } catch (Throwable ignored) {
        }
    }

    private void setContextWeakly(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public void unbind() {
        if (mWeakContext != null && mWeakContext.get() != null) {
            mWeakContext.get().unbindService(this);
        }
    }

    void attemptConnection() {
        if (sInstance != null && mWeakContext != null && mWeakContext.get() != null) {
            Intent binderIntent = new Intent("com.bubblegum.traceratops.BIND_LOGGER_SERVICE");
            binderIntent.setClassName("com.bubblegum.traceratops.app", "com.bubblegum.traceratops.app.service.LoggerService");
            mWeakContext.get().bindService(binderIntent, sInstance, Context.BIND_AUTO_CREATE);
        }
    }

    public void setShouldLog(boolean shouldLog) {
        mShouldLog = shouldLog;
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

        public Builder withTrustMode(int trustMode) {
            mInstance.mTrustMode = trustMode;
            return this;
        }

        public Builder shouldLog(boolean shouldLog) {
            mInstance.setShouldLog(shouldLog);
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

    boolean isCompatible() {
        return !mAppOutdated && !mSDKOutdated;
    }

    private static final class ERROR_CODES {
        public static final int ERROR_CODE_APP_OUTDATED = 1;
        public static final int ERROR_CODE_SDK_OUTDATED = 2;
        public static final int ERROR_CODE_SIGNATURE_VERIFICATION_FAILED = 3;
        public static final int ERROR_CODE_SIGNATURE_VERIFICATION_FAILED_MIGHT_NEED_ACTIVATION = 4;
        public static final int ERROR_CODE_TRUST_AGENT_MISSING = 5;
    }

    public static final class TrustMode {
        public static final int TRUST_MODE_OVERRIDE = 0;
        public static final int TRUST_MODE_CERTIFICATE_CHECK_ONLY = 1;
        public static final int TRUST_MODE_SIGNATURE_CHECK = 2;
    }
}
