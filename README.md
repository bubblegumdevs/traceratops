Traceratops
===========

An Open Source debugging and logging tool for the casual user

![](traceratops-app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

For more information please check [the wiki][1]



Integration guide
-----------------

1. Clone a copy of Traceratops app.
2. Build it using the same key by which you signed your app
3. Include the following dependency in your Gradle script

```groovy
compile 'com.bubblegum.traceratops:traceratops-core:0.1.0'
```
4. Include the following code in your Application class' ```onCreate()``` method to set up the SDK:

```java
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
```

5. Use ```com.bubblegum.traceratops.sdk.client.Log``` class to record logs. Optionally, you can replace all instances of ```android.util.Log``` import statements with this one.

If done correctly, you should see Log entries in the Traceratops app dashboard.

License
--------

    Copyright 2013 Bubblegum Developers

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: https://github.com/bubblegumdevs/traceratops/wiki
