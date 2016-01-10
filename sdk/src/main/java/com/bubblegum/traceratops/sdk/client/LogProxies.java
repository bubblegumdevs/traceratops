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

public class LogProxies {

    public static final LogProxy DEFAULT_LOG_PROXY = new LogProxy() {
        @Override
        public boolean d(String tag, String message) {
            android.util.Log.d(tag, message);
            return true;
        }

        @Override
        public boolean d(String tag, String message, Throwable throwable) {
            android.util.Log.d(tag, message, throwable);
            return true;
        }

        @Override
        public boolean e(String tag, String message) {
            android.util.Log.e(tag, message);
            return true;
        }

        @Override
        public boolean e(String tag, String message, Throwable throwable) {
            android.util.Log.e(tag, message, throwable);
            return true;
        }

        @Override
        public boolean v(String tag, String message) {
            android.util.Log.v(tag, message);
            return true;
        }

        @Override
        public boolean v(String tag, String message, Throwable throwable) {
            android.util.Log.v(tag, message, throwable);
            return true;
        }

        @Override
        public boolean w(String tag, String message) {
            android.util.Log.w(tag, message);
            return true;
        }

        @Override
        public boolean w(String tag, String message, Throwable throwable) {
            android.util.Log.w(tag, message, throwable);
            return true;
        }

        @Override
        public boolean wtf(String tag, String message) {
            android.util.Log.wtf(tag, message);
            return true;
        }

        @Override
        public boolean wtf(String tag, String message, Throwable throwable) {
            android.util.Log.wtf(tag, message, throwable);
            return true;
        }
    };
    
    public static final LogProxy EMPTY_LOG_PROXY = new LogProxy() {
        @Override
        public boolean d(String tag, String message) {
            return true;
        }

        @Override
        public boolean d(String tag, String message, Throwable throwable) {
            return true;
        }

        @Override
        public boolean e(String tag, String message) {
            return true;
        }

        @Override
        public boolean e(String tag, String message, Throwable throwable) {
            return true;
        }

        @Override
        public boolean v(String tag, String message) {
            return true;
        }

        @Override
        public boolean v(String tag, String message, Throwable throwable) {
            return true;
        }

        @Override
        public boolean w(String tag, String message) {
            return true;
        }

        @Override
        public boolean w(String tag, String message, Throwable throwable) {
            return true;
        }

        @Override
        public boolean wtf(String tag, String message) {
            return true;
        }

        @Override
        public boolean wtf(String tag, String message, Throwable throwable) {
            return true;
        }
    };

}
