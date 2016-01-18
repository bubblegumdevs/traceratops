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

package com.bubblegum.traceratops.demo.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubblegum.traceratops.demo.R;
import com.bubblegum.traceratops.demo.dummy.TLogDemoObject;
import com.bubblegum.traceratops.sdk.client.Debug;
import com.bubblegum.traceratops.sdk.client.Log;
import com.bubblegum.traceratops.sdk.client.TLog;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "bubblegum_mainactivity";

    private static final String DEFAULT_SIMPLE_DEBUG_STRING = "This is the default simple debug String";

    public static final class DebugKeys {
        public static final String DEBUG_KEY_SIMPLE_DEBUG_STRING = ":simpleDebugString";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button simpleLogButton = (Button) findViewById(R.id.simple_log_button);
        simpleLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Simple log event triggered");
                TLogDemoObject tLogDemoObject = new TLogDemoObject();
                TLog.d(TAG, "Simple TLog object", tLogDemoObject);
            }
        });

        Button crashButton = (Button) findViewById(R.id.simulate_crash_button);
        crashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                induceCrash();
            }
        });

        TextView simpleDebugString = (TextView) findViewById(R.id.simple_debug_string);
        simpleDebugString.setText(Debug.getString(DebugKeys.DEBUG_KEY_SIMPLE_DEBUG_STRING, DEFAULT_SIMPLE_DEBUG_STRING));
    }

    /** For testing: Induces a crash */
    private void induceCrash() {
        String myString = null;
        myString.getBytes();
    }
}
