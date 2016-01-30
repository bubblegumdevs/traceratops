/*
 *
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

import com.bubblegum.traceratops.ILoggerService;
import java.util.concurrent.atomic.AtomicInteger;

public class Ping {

    static Ping sInstance;
    private ILoggerService mLoggerService;

    Ping(ILoggerService loggerService) {
        mLoggerService = loggerService;
    }

    public static PingSession startSession(String message) {
        PingSession session = new PingSession();
        session.message = message;
        sInstance.startPingSubmitAsync(session);
        return session;
    }

    public static void endSession(PingSession session) {
        session.endSession();
        sInstance.submitPingAsync(session);
    }

    private void startPingSubmitAsync(final PingSession pingSession) {
        Traceratops.sInstance.mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                startPingSubmit(pingSession);
            }
        });
    }

    private void startPingSubmit(final PingSession pingSession) {
        if (!isLogging()) {
            Traceratops.sInstance.attemptConnection();
        }

        if (mLoggerService != null) {
            try {
                mLoggerService.pingStart(pingSession.timeStart, pingSession.message, pingSession.sessionId);
            } catch (Throwable t) {
                if(Traceratops.sInstance.mLoggerServiceConnectionCallbacks !=null) {
                    Traceratops.sInstance.mLoggerServiceConnectionCallbacks.onLoggerServiceException(t);
                }
            }
        }
    }

    private void submitPingAsync(final PingSession pingSession) {
        Traceratops.sInstance.mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                submitPing(pingSession);
            }
        });
    }

    private void submitPing(PingSession pingSession) {
        if (!isLogging()) {
            Traceratops.sInstance.attemptConnection();
        }

        if (mLoggerService != null) {
            try {
                mLoggerService.pingEnd(pingSession.timeStart, pingSession.timeEnd, pingSession.message, pingSession.sessionId);
            } catch (Throwable t) {
                if(Traceratops.sInstance.mLoggerServiceConnectionCallbacks !=null) {
                    Traceratops.sInstance.mLoggerServiceConnectionCallbacks.onLoggerServiceException(t);
                }
            }
        }
    }

    public static class PingSession {
        private long timeStart;
        private long timeEnd;
        private String message;
        private int sizeInBytes;
        private static final AtomicInteger tokenGenerator = new AtomicInteger(0);
        private final int sessionId;

        PingSession() {
            timeStart = System.currentTimeMillis();
            sessionId = tokenGenerator.incrementAndGet();
        }

        public void endSession() {
            timeEnd = System.currentTimeMillis();
        }

        public static void tick(int sizeInBytes) {
            // TODO: 1/30/16 Implement tick
        }
    }

    private boolean isLogging() {
        return Traceratops.sInstance.mIsSafe && mLoggerService!=null;
    }
}
