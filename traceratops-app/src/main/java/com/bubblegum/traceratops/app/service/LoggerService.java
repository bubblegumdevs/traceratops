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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.bubblegum.traceratops.ILoggerService;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.model.LogEntry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggerService extends Service {
    private static final String TAG = "bubblegum_loggerservice";

    ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new ILoggerService.Stub() {

        @Override
        public void log(String tag, String message, String stackTrace, int level) throws RemoteException {
            queueLogTask(tag, message, stackTrace, level);
        }
    };

    private void queueLogTask(String tag, String message, String stackTrace, int level) {
        mExecutorService.submit(new LogTask(tag, message, stackTrace, level, System.currentTimeMillis()));
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
}
