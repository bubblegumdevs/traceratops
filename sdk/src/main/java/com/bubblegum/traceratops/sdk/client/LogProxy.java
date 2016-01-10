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

// TODO Add javadocs for all methods

public interface LogProxy {

    String W = "w";
    String V = "v";
    String E = "e";
    String D = "d";
    String WTF = "wtf";

    boolean d(String tag, String message);
    boolean d(String tag, String message, Throwable throwable);
    boolean e(String tag, String message);
    boolean e(String tag, String message, Throwable throwable);
    boolean v(String tag, String message);
    boolean v(String tag, String message, Throwable throwable);
    boolean w(String tag, String message);
    boolean w(String tag, String message, Throwable throwable);
    boolean wtf(String tag, String message);
    boolean wtf(String tag, String message, Throwable throwable);
}
