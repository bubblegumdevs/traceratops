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

package com.bubblegum.traceratops;

interface ILoggerService {

    void log(String tag, String message, String stackTrace, int level);
    void tlog(String tag, String message, in Bundle args, int level);
    String getString(String tag, String defaultValue);
    boolean getBoolean(String tag, boolean defaultValue);
    void crash(String stacktrace, String message);
    void pingStart(long startTime, String message, int token);
    void pingEnd(long startTime, long endTime, String message, int token);
    void pingTick(long timetamp, int sizeInBytes, int token);
}
