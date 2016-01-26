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

package com.bubblegum.traceratops.app.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.ui.fragments.CrashDetailsFragment;

public class CrashDetailsActivity extends AppCompatActivity {

    private static final String CRASH_DETAIL_FRAGMENT_TAG = ":crash:detail";

    private CrashDetailsFragment fragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_object_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragment = new CrashDetailsFragment();
        Bundle args = getIntent().getExtras();
        getSupportActionBar().setTitle(R.string.crash_detail_activity_title);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, CRASH_DETAIL_FRAGMENT_TAG).commit();
    }
}
