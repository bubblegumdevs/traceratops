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

package com.bubblegum.traceratops.demo;

import android.app.Application;

import com.bubblegum.traceratops.sdk.client.Log;
import com.bubblegum.traceratops.sdk.client.LogProxies;
import com.bubblegum.traceratops.sdk.client.Traceratops;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Traceratops.setup(this)
                .withServiceConnectionCallbacks(new Traceratops.LoggerServiceConnectionCallbacks() {
                    @Override
                    public void onLoggerServiceConnected() {

                    }

                    @Override
                    public void onLoggerServiceDisconnected() {

                    }

                    @Override
                    public void onLoggerServiceException(Throwable t) {

                    }
                })
                .withLogProxy(LogProxies.EMPTY_LOG_PROXY)
                .shouldLog(true)
                .handleCrashes(this)
                .connect();
    }

    /** For testing: Induces a crash */
    private void induceCrash() {
        String myString = null;
        myString.getBytes();
    }
}
