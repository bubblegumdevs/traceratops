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

package com.bubblegum.traceratops.app.ui.adapters.plugins;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.CrashEntry;
import com.bubblegum.traceratops.app.ui.activities.CrashDetailsActivity;
import com.bubblegum.traceratops.app.ui.fragments.CrashDetailsFragment;
import com.bubblegum.traceratops.app.ui.utils.CircularTextDrawable;

public class CrashAdapterPlugin extends AbsAdapterPlugin <CrashEntry> {

    private Drawable mDrawable = new CircularTextDrawable("!", Color.RED);

    @Override
    public Class<CrashEntry> getSupportedClass() {
        return CrashEntry.class;
    }

    @Override
    public String getPrimaryText(CrashEntry entry) {
        return "CRASH!";
    }

    @Override
    public String getSecondaryText(CrashEntry entry) {
        return entry.message;
    }

    @Override
    public long getTimestamp(CrashEntry entry) {
        return entry.timestamp;
    }

    @Override
    public Drawable getImageDrawable(CrashEntry entry) {
        return mDrawable;
    }

    @Override
    protected void onPrimaryButtonClicked(CrashEntry entry) {
        Intent crashIntent = new Intent(getContext(), CrashDetailsActivity.class);
        crashIntent.putExtra(CrashDetailsFragment.EXTRA_CRASH_MSG, entry.message);
        crashIntent.putExtra(CrashDetailsFragment.EXTRA_CRASH_STACKTRACE, entry.stacktrace);
        getContext().startActivity(crashIntent);
    }

    @Override
    protected void onSecondaryButtonClicked(CrashEntry entry) {
        copyText(entry.stacktrace);
        Toast.makeText(getContext(), R.string.crash_entry_copied_to_clipboard, Toast.LENGTH_LONG).show();
    }

    private void copyText(String textToBeCopied) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Traceratops",textToBeCopied);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    protected String getPrimaryActionText() {
        return getContext().getString(R.string.view);
    }

    @Override
    protected String getSecondaryActionText() {
        return getContext().getString(R.string.copy);
    }
}
