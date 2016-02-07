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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bubblegum.traceratops.app.R;

public class CrashDetailsFragment extends BaseFragment {

    public static final String EXTRA_CRASH_MSG = ":crash:message";
    public static final String EXTRA_CRASH_STACKTRACE = ":crash:stacktrace";

    private String mStacktrace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_crash_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        TextView messageView = (TextView) view.findViewById(R.id.crash_message_tv);
        final TextView stacktraceView = (TextView) view.findViewById(R.id.crash_stacktrace_tv);

        String message = null;
        if (getArguments() != null) {
            message = getArguments().getString(EXTRA_CRASH_MSG);
            mStacktrace = getArguments().getString(EXTRA_CRASH_STACKTRACE);
        }

        if (message != null) {
            messageView.setText(message);
        }
        if (mStacktrace != null) {
            stacktraceView.setText(mStacktrace);

            // Copy stacktrace on clicking
            stacktraceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cbMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("stacktrace", stacktraceView.getText());
                    cbMan.setPrimaryClip(clipData);
                    Toast.makeText(getActivity(), R.string.crash_entry_copied_to_clipboard, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_crash_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mStacktrace);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));
                break;
            default:
                break;
        }
        return false;
    }
}
