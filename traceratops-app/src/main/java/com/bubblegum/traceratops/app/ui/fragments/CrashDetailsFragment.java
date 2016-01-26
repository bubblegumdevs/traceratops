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

package com.bubblegum.traceratops.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.CrashEntry;

public class CrashDetailsFragment extends BaseFragment {

    private static final String EXTRA_CRASH_MSG = ":crash:message";
    private static final String EXTRA_CRASH_STACKTRACE = ":crash:stacktrace";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_crash_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView messageView = (TextView) view.findViewById(R.id.crash_message_tv);
        TextView stacktraceView = (TextView) view.findViewById(R.id.crash_stacktrace_tv);

        String message = null;
        String stacktrace = null;
        if (getArguments() != null) {
            message = getArguments().getString(EXTRA_CRASH_MSG);
            stacktrace = getArguments().getString(EXTRA_CRASH_STACKTRACE);
        }

        if (message != null) {
            messageView.setText(message);
        }
        if (stacktrace != null) {
            stacktraceView.setText(stacktrace);
        }
    }
}
