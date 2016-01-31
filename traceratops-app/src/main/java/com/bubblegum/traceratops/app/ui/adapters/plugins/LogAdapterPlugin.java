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

package com.bubblegum.traceratops.app.ui.adapters.plugins;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bubblegum.traceratops.app.LogStub;
import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.model.LogEntry;
import com.bubblegum.traceratops.app.ui.activities.BaseActivity;
import com.bubblegum.traceratops.app.ui.utils.CircularTextDrawable;

public class LogAdapterPlugin extends AbsAdapterPlugin<LogEntry> {

    public LogAdapterPlugin(BaseActivity activity) {
        super(activity);
    }

    @Override
    public Class<LogEntry> getSupportedClass() {
        return LogEntry.class;
    }

    @Override
    public String getPrimaryText(LogEntry entry) {
        return entry.description;
    }

    @Override
    public String getSecondaryText(LogEntry entry) {
        return entry.tag;
    }

    @Override
    public long getTimestamp(LogEntry entry) {
        return entry.timestamp;
    }

    @Override
    public Drawable getImageDrawable(LogEntry entry) {
        return new CircularTextDrawable(LogStub.getTextFromLevel(entry.level), LogStub.getColorForLevel(getBaseActivity(), entry.level));
    }

    @Override
    protected void onPrimaryButtonClicked(LogEntry entry) {
        copyText(entry.description);
        getBaseActivity().showSnackbarOrToast(R.string.log_entry_copied_to_clipboard, BaseActivity.LENGTH_LONG, null, null);
    }

    @Override
    protected void onSecondaryButtonClicked(LogEntry entry) {

    }

    private void copyText(String textToBeCopied) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getBaseActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Traceratops",textToBeCopied);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    protected String getPrimaryActionText() {
        return getBaseActivity().getString(R.string.copy);
    }

    @Override
    protected String getSecondaryActionText() {
        return null;
    }
}
