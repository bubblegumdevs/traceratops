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

public class TLog {

    static TLog sInstance;

    TLog() {

    }
    
    public static void v(String tag, String message, Object object) {
        sInstance.vInternal(tag, message, object);
    }

    public static void d(String tag, String message, Object object) {
        sInstance.dInternal(tag, message, object);
    }

    public static void w(String tag, String message, Object object) {
        sInstance.wInternal(tag, message, object);
    }

    public static void e(String tag, String message, Object object) {
        sInstance.eInternal(tag, message, object);
    }

    public static void i(String tag, String message, Object object) {
        sInstance.iInternal(tag, message, object);
    }

    public static void wtf(String tag, String message, Object object) {
        sInstance.wtfInternal(tag, message, object);
    }
    
    private void dInternal(String tag, String message, Object object) {
        Log.sInstance.logInternal(tag, message, object, LogProxy.D);
    }
    
    private void eInternal(String tag, String message, Object object) {
        Log.sInstance.logInternal(tag, message, object, LogProxy.E);
    }

    private void iInternal(String tag, String message, Object object) {
        Log.sInstance.logInternal(tag, message, object, LogProxy.I);
    }

    private void vInternal(String tag, String message, Object object) {
        Log.sInstance.logInternal(tag, message, object, LogProxy.V);
    }

    private void wInternal(String tag, String message, Object object) {
        Log.sInstance.logInternal(tag, message, object, LogProxy.W);
    }

    private void wtfInternal(String tag, String message, Object object) {
        Log.sInstance.logInternal(tag, message, object, LogProxy.WTF);
    }
    
}
