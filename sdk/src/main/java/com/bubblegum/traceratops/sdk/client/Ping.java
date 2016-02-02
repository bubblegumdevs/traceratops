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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Ping {

    static Ping sInstance;
    private ILoggerService mLoggerService;

    Ping(ILoggerService loggerService) {
        mLoggerService = loggerService;
    }

    public static PingSession startSession(String message) {
        if (sInstance != null) {
            PingSession session = new PingSession();
            session.message = message;
            sInstance.submitStartPingAsync(session);
            return session;
        }
        return null;
    }

    public static void endSession(PingSession session) {
        if (sInstance != null && session != null) {
            session.endSession();
            sInstance.submitPingAsync(session);
        }
    }

    public static void tick(PingSession session, int sizeInBytes) {
        if (sInstance != null && session != null) {
            PingSession.Tick tick = session.tick(sizeInBytes);
            sInstance.submitTickAsync(tick, session);
        }
    }

    public static void tick(PingSession session) {
        tick(session, 0);
    }

    private void submitStartPingAsync(final PingSession pingSession) {
        Traceratops.sInstance.mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                submitStartPing(pingSession);
            }
        });
    }

   private void submitStartPing(final PingSession pingSession) {
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

    private void submitTickAsync(final PingSession.Tick tick, final PingSession pingSession) {
        Traceratops.sInstance.mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                submitTick(tick, pingSession);
            }
        });
    }

    private void submitTick(final PingSession.Tick tick, PingSession pingSession) {
        if (mLoggerService != null) {
            try {
                mLoggerService.pingTick(tick.timestamp, tick.sizeInBytes, pingSession.message, pingSession.sessionId);
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
        private static final AtomicInteger tokenGenerator = new AtomicInteger(0);
        private final int sessionId;

        PingSession() {
            timeStart = System.currentTimeMillis();
            sessionId = tokenGenerator.incrementAndGet();
        }

        private void endSession() {
            timeEnd = System.currentTimeMillis();
        }

        private Tick tick(int sizeInBytes) {
            Tick tick = new Tick(System.currentTimeMillis(), sizeInBytes);
            return tick;
        }

        private class Tick {
            Tick(long timestamp, int sizeInBytes) {
                this.timestamp = timestamp;
                this.sizeInBytes = sizeInBytes;
            }

            public long timestamp;
            public int sizeInBytes;
        }
    }

    private boolean isLogging() {
        return Traceratops.sInstance.mIsSafe && mLoggerService!=null && Traceratops.sInstance.isCompatible();
    }
}
