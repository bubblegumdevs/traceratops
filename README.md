Traceratops
===========

An Open Source debugging and logging tool for the casual user

![](traceratops-app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

For more information please check [the wiki][1]



Integration guide
-----------------

1. Download Traceratops app from the [Play Store][ps]. You can scan the QR code below.
   
   ![](https://cloud.githubusercontent.com/assets/1681767/12774900/4d20ccb8-ca6f-11e5-86aa-14ec68cb9096.png)
2. Clone this repo.
   (NOTE: If you are looking to just try out this SDK, you can skip step 2, 3 and 4. However, please follow these steps when you plan to go on production. More details in [the wiki][1])
3. In the trustagent module's AndroidManifest.xml, replace ```com.bubblegum.traceratops.demo.trust``` with ```<your package name>.trust``` and ```com.bubblegum.traceratops.demo.TRUST``` with ```<your package name>.TRUST```. Do the same in trustagent's build.gradle file.
4. Now build trustagent. Use the release key to sign trustagent (the same release key with which you sign your app in production). After building is complete, install the output APK in all the devices where you would like to use Traceratops.
5. Now include the SDK in your app. To do that, include the following dependency in your app's build.gradle

   ```groovy
   compile 'com.bubblegum.traceratops:traceratops-core:0.2.0'
   ```
6. Add the following code in your app's Application class' ```onCreate()``` method to set up the SDK:

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
   If you have skipped step 2, 3 and 4, add ```.withTrustMode(TrustMode.TRUST_MODE_OVERRIDE)``` to the Builder methods.
7. Use ```com.bubblegum.traceratops.sdk.client.Log``` class to record logs. Optionally, you can replace all instances of ```android.util.Log``` import statements with this one.

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
 [ps]: https://play.google.com/store/apps/details?id=com.bubblegum.traceratops.app
