Traceratops
===========
[ ![Download](https://api.bintray.com/packages/bubblegumdevs/Bubblegum/traceratops-sdk/images/download.svg) ](https://bintray.com/bubblegumdevs/Bubblegum/traceratops-sdk/_latestVersion)

An Open Source debugging and logging tool for the casual user

![](traceratops-app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

For more information please check [the wiki][1]



Integration guide
-----------------

1. Download Traceratops app from the [Play Store](https://play.google.com/store/apps/details?id=com.bubblegum.traceratops.app). You can scan the QR code below.
   
   ![](https://cloud.githubusercontent.com/assets/1681767/12774900/4d20ccb8-ca6f-11e5-86aa-14ec68cb9096.png)
1. Set up the trust agent by following [these steps][2].  
_**NOTE**: You can skip this step if you just want to try out the SDK. However, if you are planning to launch your app to production, it is strongly recommended that you complete this step._
1. Now include the SDK in your app. To do that, include the following dependency in your app's build.gradle:

   ```groovy
   compile 'com.bubblegum.traceratops:traceratops-core:0.2.0'
   ```
1. Add the following code in your app's Application class' ```onCreate()``` method to set up the SDK:

    ```java
    Traceratops.setup(this)
            .handleCrashes(true)
            .connect();
    ```
   If you have skipped the trust agent setup, add ```.withTrustMode(TrustMode.TRUST_MODE_OVERRIDE)``` to the above code. To find out more about trust agents and TrustModes, click [here][3].
1. Use ```com.bubblegum.traceratops.sdk.client.Log``` class to record logs. Optionally, you can replace all instances of ```android.util.Log``` import statements with this one.

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
 [2]: https://github.com/bubblegumdevs/traceratops/wiki/trust-agent-setup
 [3]: https://github.com/bubblegumdevs/traceratops/wiki/why-trust-agent
 [ps]: https://play.google.com/store/apps/details?id=com.bubblegum.traceratops.app
