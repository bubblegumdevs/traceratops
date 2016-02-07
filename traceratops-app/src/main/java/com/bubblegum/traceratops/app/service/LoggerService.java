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
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import com.bubblegum.traceratops.ILoggerService;
import com.bubblegum.traceratops.app.BuildConfig;
import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.CrashEntry;
import com.bubblegum.traceratops.app.model.LogEntry;
import com.bubblegum.traceratops.app.model.PingEntry;
import com.bubblegum.traceratops.app.model.TLogEntry;
import com.bubblegum.traceratops.app.profiles.AppProfile;
import com.bubblegum.traceratops.app.ui.activities.CrashDetailsActivity;
import com.bubblegum.traceratops.app.ui.activities.MainActivity;
import com.bubblegum.traceratops.app.ui.fragments.CrashDetailsFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggerService extends Service {
    private static final String TAG = "bubblegum_loggerservice";

    public static final String ACTION_ERROR = "com.bubblegum.traceratops.app.SERVICE_ERROR";
    public static final String EXTRA_ERROR_CODE = ":traceratops:loggerService:errorCode";

    private static final int MIN_SDK_VERSION = 1;

    private SparseArray<String> mPidMap = new SparseArray<>();

    ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        showPersistentNotification();
        return new TraceratopsBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        hidePersistentNotification();
        return super.onUnbind(intent);
    }

    private class TraceratopsBinder extends ILoggerService.Stub {

        @Override
        public void log(String tag, String message, String stackTrace, int level) throws RemoteException {
            queueLogTask(tag, message, stackTrace, level, Binder.getCallingPid());
        }

        @Override
        public void tlog(String tag, String message, Bundle args, int level) throws RemoteException {
            queueTLogTask(tag, message, args, level, Binder.getCallingPid());
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
            queueCrashTask(stacktrace, message, Binder.getCallingPid());
        }

        @Override
        public void pingStart(long startTime, String message, int token) throws RemoteException {
            queuePingStartTask(startTime, message, token, Binder.getCallingPid());
        }

        @Override
        public void pingEnd(long startTime, long endTime, String message, int token) throws RemoteException {
            queuePingEndTask(startTime, endTime, message, token, Binder.getCallingPid());
        }

        @Override
        public void pingTick(long timetamp, int sizeInBytes, String message, int token) throws RemoteException {
            queuePingTickTask(timetamp, sizeInBytes, message, token, Binder.getCallingPid());
        }

        @Override
        public int checkVersion(int sdkVersion) throws RemoteException {
            if (sdkVersion < MIN_SDK_VERSION) {
                return -1;
            }
            return BuildConfig.VERSION_CODE;
        }

        @Override
        public void reportError(int errorCode) throws RemoteException {
            Intent errorIntent = new Intent(ACTION_ERROR);
            errorIntent.putExtra(EXTRA_ERROR_CODE, errorCode);
            sendBroadcast(errorIntent);
            AppProfile appProfile = getAppProfileForPid(Binder.getCallingPid());
            if(appProfile!=null) {
                appProfile.setErrorCode(errorCode);
            }
        }

        @Override
        public void reportPackage(String packageName) throws RemoteException {
            mPidMap.put(Binder.getCallingPid(), packageName);
            TraceratopsApplication.from(LoggerService.this).makeAppProfile(this, packageName);
        }

        private void queuePingStartTask(long timeStart, String message, int token, int pid) {
            mExecutorService.submit(new PingTask(timeStart, message, token, System.currentTimeMillis(), pid));
        }

        private void queuePingEndTask(long timeStart, long timeEnd, String message, int token, int pid) {
            mExecutorService.submit(new PingTask(timeStart, timeEnd, message, token, System.currentTimeMillis(), pid));
        }

        private void queuePingTickTask(long tickTimestamp, int sizeInBytes, String message, int token, int pid) {
            mExecutorService.submit(new PingTask(tickTimestamp, sizeInBytes, message, token, pid));
        }

        private void queueTLogTask(String tag, String message, Bundle bundle, int level, int pid) {
            mExecutorService.submit(new TLogTask(tag, message, bundle, level, System.currentTimeMillis(), pid));
        }

        private void queueLogTask(String tag, String message, String stackTrace, int level, int pid) {
            mExecutorService.submit(new LogTask(tag, message, stackTrace, level, System.currentTimeMillis(), pid));
        }

        private void queueCrashTask(String stacktrace, String message, int pid) {
            mExecutorService.submit(new CrashTask(message, stacktrace, System.currentTimeMillis(), pid));

        /* Crash notification:
         * Clicking on this notification should take the user directly to that particular crash page
         */
            NotificationManager notifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(LoggerService.this, CrashDetailsActivity.class);
            notificationIntent.putExtra(CrashDetailsFragment.EXTRA_CRASH_MSG, message);
            notificationIntent.putExtra(CrashDetailsFragment.EXTRA_CRASH_STACKTRACE, stacktrace);
            PendingIntent contentIntent = PendingIntent.getActivity(LoggerService.this, R.id.traceratops_crash_pending_intent, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(LoggerService.this);
            builder.setSmallIcon(R.drawable.ic_notif_default)
                    .setColor(ContextCompat.getColor(LoggerService.this, R.color.colorPrimary))
                    .setContentTitle(LoggerService.this.getString(R.string.crash_notification_title))
                    .setContentText(LoggerService.this.getString(R.string.crash_notification_text))
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ERROR)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);
            notifMan.notify(R.id.traceratops_crash_id, builder.build());
        }


        private class LogTask implements Runnable {

            final LogEntry logEntry;
            int pid;

            public LogTask(String tag, String message, String stackTrace, int level, long timestamp, int pid) {
                logEntry = new LogEntry();
                logEntry.tag = tag;
                logEntry.description = message;
                logEntry.level = level;
                logEntry.stackTrace = stackTrace;
                logEntry.timestamp = timestamp;
                this.pid = pid;
            }

            @Override
            public void run() {
                AppProfile appProfile = getAppProfileForPid(pid);
                if(appProfile!=null) {
                    appProfile.addEntry(logEntry);
                }
                // TODO add database insertion code
            }
        }

        private class CrashTask implements Runnable {

            final CrashEntry crashEntry;
            int pid;

            public CrashTask(String message, String stacktrace, long timestamp, int pid) {
                crashEntry = new CrashEntry();
                crashEntry.message = message;
                crashEntry.stacktrace = stacktrace;
                crashEntry.timestamp = timestamp;
                this.pid = pid;
            }

            @Override
            public void run() {
                AppProfile appProfile = getAppProfileForPid(pid);
                if(appProfile!=null) {
                    appProfile.addEntry(crashEntry);
                }
            }
        }

        private class TLogTask implements Runnable {

            final TLogEntry logEntry;
            int pid;

            public TLogTask(String tag, String message, Bundle args, int level, long timestamp, int pid) {
                logEntry = new TLogEntry();
                logEntry.tag = tag;
                logEntry.description = message;
                logEntry.level = level;
                logEntry.args = args;
                logEntry.timestamp = timestamp;
                this.pid = pid;
            }

            @Override
            public void run() {
                AppProfile appProfile = getAppProfileForPid(pid);
                if(appProfile!=null) {
                    appProfile.addEntry(logEntry);
                }
                // TODO add database insertion code
            }
        }

        private class PingTask implements Runnable {

            final PingEntry pingEntry;
            int pid;

            /**
             * Records a finished ping
             *
             * @param timeStart Start time of ping
             * @param timeEnd   End time of ping
             * @param message   Message to log along with ping
             * @param token     Token associated with the ping session
             */
            public PingTask(long timeStart, long timeEnd, String message, int token, long timestamp, int pid) {
                pingEntry = new PingEntry();
                pingEntry.timestampBegin = timeStart;
                pingEntry.timestampEnd = timeEnd;
                pingEntry.message = message;
                pingEntry.token = token;
                pingEntry.timestamp = timestamp;
                this.pid = pid;
            }

            /**
             * Records the start of a ping
             *
             * @param timeStart Start time of ping
             * @param message   Message to log along with ping
             * @param token     Token associated with the ping session
             */
            public PingTask(long timeStart, String message, int token, long timestamp, int pid) {
                pingEntry = new PingEntry();
                pingEntry.timestampBegin = timeStart;
                pingEntry.timestampEnd = 0;
                pingEntry.message = message;
                pingEntry.token = token;
                pingEntry.timestamp = timestamp;
                this.pid = pid;
            }

            /**
             * Records a tick to indicate some activity associated with an unfinished ping
             *
             * @param tickTimestamp Time of tick occurrence
             * @param sizeInBytes   Size of data sent / received in bytes
             * @param token         Token associated with the ping session
             */
            public PingTask(long tickTimestamp, int sizeInBytes, String message, int token, int pid) {
                pingEntry = new PingEntry();
                pingEntry.timestamp = tickTimestamp;
                pingEntry.sizeInBytes = sizeInBytes;
                pingEntry.token = token;
                pingEntry.timestampBegin = 0;
                pingEntry.timestampEnd = 0;
                pingEntry.message = message;
                this.pid = pid;
            }

            @Override
            public void run() {
                AppProfile appProfile = getAppProfileForPid(pid);
                if(appProfile!=null) {
                    appProfile.addEntry(pingEntry);
                }
                // TODO add database insertion code
            }
        }
    }


    private void showPersistentNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, R.id.traceratops_notification_pending_intent, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
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

    public static final class ERROR_CODES {
        public static final int ERROR_CODE_APP_OUTDATED = 1;
        public static final int ERROR_CODE_SDK_OUTDATED = 2;
        public static final int ERROR_CODE_SIGNATURE_VERIFICATION_FAILED = 3;
        public static final int ERROR_CODE_SIGNATURE_VERIFICATION_FAILED_MIGHT_NEED_ACTIVATION = 4;
        public static final int ERROR_CODE_TRUST_AGENT_MISSING = 5;
    }

    private AppProfile getAppProfileForPid(int pid) {
        String packagName = mPidMap.get(pid);
        return TraceratopsApplication.from(this).getProfile(packagName);
    }
}
