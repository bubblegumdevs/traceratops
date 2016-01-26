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

package com.bubblegum.traceratops.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.bubblegum.traceratops.ILoggerService;
import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.CrashEntry;
import com.bubblegum.traceratops.app.model.LogEntry;
import com.bubblegum.traceratops.app.model.TLogEntry;
import com.bubblegum.traceratops.app.ui.activities.CrashDetailsActivity;
import com.bubblegum.traceratops.app.ui.activities.MainActivity;
import com.bubblegum.traceratops.app.ui.fragments.CrashDetailsFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggerService extends Service {
    private static final String TAG = "bubblegum_loggerservice";

    ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        showPersistentNotification();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        hidePersistentNotification();
        return super.onUnbind(intent);
    }

    private IBinder mBinder = new ILoggerService.Stub() {

        @Override
        public void log(String tag, String message, String stackTrace, int level) throws RemoteException {
            queueLogTask(tag, message, stackTrace, level);
        }

        @Override
        public void tlog(String tag, String message, Bundle args, int level) throws RemoteException {
            queueTLogTask(tag, message, args, level);
        }

        @Override
        public String getString(String key, String defaultValue) throws RemoteException {
            return PreferenceManager.getDefaultSharedPreferences(LoggerService.this).getString(key, defaultValue);
        }

        @Override
        public boolean getBoolean(String key, boolean defaultValue) throws RemoteException {
            return PreferenceManager.getDefaultSharedPreferences(LoggerService.this).getBoolean(key, defaultValue);
        }

        @Override
        public void crash(String stacktrace, String message) throws RemoteException {
            queueCrash(stacktrace, message);
        }
    };

    private void queueTLogTask(String tag, String message, Bundle bundle, int level) {
        mExecutorService.submit(new TLogTask(tag, message, bundle, level, System.currentTimeMillis()));
    }

    private void queueLogTask(String tag, String message, String stackTrace, int level) {
        mExecutorService.submit(new LogTask(tag, message, stackTrace, level, System.currentTimeMillis()));
    }

    private void queueCrash(String stacktrace, String message) {
        mExecutorService.submit(new CrashTask(message, stacktrace, System.currentTimeMillis()));

        /* Crash notification:
         * Clicking on this notification should take the user directly to that particular crash page
         */
        NotificationManager notifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, CrashDetailsActivity.class);
        notificationIntent.putExtra(CrashDetailsFragment.EXTRA_CRASH_MSG, message);
        notificationIntent.putExtra(CrashDetailsFragment.EXTRA_CRASH_STACKTRACE, stacktrace);
        PendingIntent contentIntent = PendingIntent.getActivity(this, R.id.traceratops_crash_pending_intent, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notif_default)
                .setContentTitle(getString(R.string.crash_notification_title))
                .setContentText(getString(R.string.crash_notification_text))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        notifMan.notify(R.id.traceratops_crash_id, builder.build());
    }

    private class LogTask implements Runnable {

        final LogEntry logEntry;

        public LogTask(String tag, String message, String stackTrace, int level, long timestamp) {
            logEntry = new LogEntry();
            logEntry.tag = tag;
            logEntry.description = message;
            logEntry.level = level;
            logEntry.stackTrace = stackTrace;
            logEntry.timestamp = timestamp;
        }

        @Override
        public void run() {
            TraceratopsApplication.from(getApplication()).addEntry(logEntry);
            // TODO add database insertion code
        }
    }

    private class CrashTask implements Runnable {

        final CrashEntry crashEntry;

        public CrashTask(String message, String stacktrace, long timestamp) {
            crashEntry = new CrashEntry();
            crashEntry.message = message;
            crashEntry.stacktrace = stacktrace;
            crashEntry.timestamp = timestamp;
        }

        @Override
        public void run() {
            TraceratopsApplication.from(getApplication()).addEntry(crashEntry);
        }
    }

    private class TLogTask implements Runnable {

        final TLogEntry logEntry;

        public TLogTask(String tag, String message, Bundle args, int level, long timestamp) {
            logEntry = new TLogEntry();
            logEntry.tag = tag;
            logEntry.description = message;
            logEntry.level = level;
            logEntry.args = args;
            logEntry.timestamp = timestamp;
        }

        @Override
        public void run() {
            TraceratopsApplication.from(getApplication()).addEntry(logEntry);
            // TODO add database insertion code
        }
    }

    private void showPersistentNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, R.id.traceratops_notification_pending_intent, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getString(R.string.persistent_notification_title))
                .setContentText(getString(R.string.persistent_notification_text))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notif_default)
                .build();
        nm.notify(R.id.traceratops_running_id, notification);
    }

    private void hidePersistentNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(R.id.traceratops_running_id);
    }
}
