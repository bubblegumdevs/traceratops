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
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.ILoggerService;

public final class Log implements LogProxy, ServiceConnection {

    private static Log sInstance;

    private ILoggerService mLoggerService;
    private OnLoggerServiceExceptionListener mOnLoggerServiceExceptionListener;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLoggerService = ILoggerService.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLoggerService = null;
    }

    private interface OnLoggerServiceExceptionListener {

        void onLoggerServiceException(Throwable t);

    }

    public static Log getInstance(@NonNull Context context, @Nullable OnLoggerServiceExceptionListener onLoggerServiceExceptionListener) {
        if(sInstance == null) {
            sInstance = new Log(onLoggerServiceExceptionListener);
            Intent binderIntent = new Intent(ILoggerService.class.getName());
            binderIntent.setClassName("com.bubblegum.traceratops", "com.bubblegum.traceratops.app.service.LoggerService");
            context.bindService(binderIntent, sInstance, Context.BIND_AUTO_CREATE);
        }
        return sInstance;
    }

    private Log(@Nullable OnLoggerServiceExceptionListener onLoggerServiceExceptionListener) {
        mOnLoggerServiceExceptionListener = onLoggerServiceExceptionListener;
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

    private void logInternal(String tag, String message, Throwable throwable, String level) {
        try {
            if (mLoggerService != null) {
                mLoggerService.log(tag, message, throwable.getMessage(), level); // TODO for now sending throwable's message. Need to send entire stack trace
            }
        } catch (Throwable t) {
            if(mOnLoggerServiceExceptionListener!=null) {
                mOnLoggerServiceExceptionListener.onLoggerServiceException(t);
            }
        }
    }

}
