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
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bubblegum.traceratops.ILoggerService;

public class LoggerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new ILoggerService.Stub() {

        @Override
        public void log(String tag, String message, String stackTrace, String level) throws RemoteException {
            logImpl(tag, message, stackTrace, level);
            // TODO Need to write proper implementation to reflect Logs onto UI
        }
    };

    private void logImpl(String tag, String message, String stackTrace, String level) {
        Toast.makeText(LoggerService.this, message, Toast.LENGTH_SHORT).show();
        Log.e("BBGM", "Ta Ta");
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(2000);
    }
}
