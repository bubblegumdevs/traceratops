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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bubblegum.traceratops.app.LogStub;
import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.TraceratopsApplication;
import com.bubblegum.traceratops.app.ui.adapters.filters.BaseEntryFilter;
import com.bubblegum.traceratops.app.ui.adapters.filters.CrashFilter;
import com.bubblegum.traceratops.app.ui.adapters.filters.LevelFilter;
import com.bubblegum.traceratops.app.ui.adapters.filters.LogTagFilter;

import java.util.List;

public class LogFilterFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private SeekBar mLevelSeekbar;
    private Switch mCrashSwitch;
    private TextView mTagTextTv;
    private EditText mTagValueEt;
    private TextView mLogLevelIndicator;

    private String[] mLogLevels;
    private int mCurrentLogLevel = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyFilters();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.clear_filters, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearFilters();
                    }
                })
                .setTitle(R.string.filter_title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View filterView = inflater.inflate(R.layout.fragment_log_filter, null);

        mLevelSeekbar = (SeekBar) filterView.findViewById(R.id.level_seekbar);
        mCrashSwitch = (Switch) filterView.findViewById(R.id.crash_switch);
        mTagTextTv = (TextView) filterView.findViewById(R.id.tag_tv);
        mTagValueEt = (EditText) filterView.findViewById(R.id.tag_value);
        mLogLevelIndicator = (TextView) filterView.findViewById(R.id.level_info_tv);
        setupSeekbar();
        mLevelSeekbar.setOnSeekBarChangeListener(this);
        mCrashSwitch.setOnCheckedChangeListener(this);

        prefillExistingFilters();

        builder.setView(filterView);
        return builder.create();
    }

    private void setupSeekbar() {
        mLogLevels = getResources().getStringArray(R.array.log_levels);
        mLevelSeekbar.setMax(mLogLevels.length - 1);
        int progress = mLevelSeekbar.getProgress();
        if (progress < mLogLevels.length) {
            mLogLevelIndicator.setText(mLogLevels[progress]);
        }
    }

    private void prefillExistingFilters() {
        List<BaseEntryFilter> filters = TraceratopsApplication.from(getActivity()).getCurrentFilters();
        for(BaseEntryFilter filter : filters) {
            prefillFilter(filter);
        }
    }

    private void prefillFilter(BaseEntryFilter filter) {
        if(filter instanceof CrashFilter) {
            mCrashSwitch.setChecked(true);
        } else if (filter instanceof  LogTagFilter) {
            String tag = ((LogTagFilter) filter).getTag();
            mTagValueEt.setText(tag);
        } else if (filter instanceof LevelFilter) {
            int progress = ((LevelFilter) filter).getLogLevel();
            mLevelSeekbar.setProgress(progress-LogStub.V);
        }

    }

    private void applyFilters() {
        clearFilters();
        if (mCrashSwitch.isChecked()) {
            CrashFilter crashFilter = new CrashFilter();
            TraceratopsApplication.from(getActivity()).addFilter(crashFilter);
        } else {
            if (mLevelSeekbar.getProgress() > 0) {
                LevelFilter levelFilter = new LevelFilter(mLevelSeekbar.getProgress() + LogStub.V);
                TraceratopsApplication.from(getActivity()).addFilter(levelFilter);
            }
            if (!TextUtils.isEmpty(mTagValueEt.getText())) {
                LogTagFilter tagFilter = new LogTagFilter(mTagValueEt.getText());
                TraceratopsApplication.from(getActivity()).addFilter(tagFilter);
            }
        }
    }

    private void clearFilters() {
        TraceratopsApplication.from(getActivity()).clearFilters();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress < mLogLevels.length) {
            mLogLevelIndicator.setText(mLogLevels[progress]);
        } else {
            mLogLevelIndicator.setText("??");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mLevelSeekbar.setEnabled(!isChecked);
        mTagValueEt.setEnabled(!isChecked);
        mTagTextTv.setEnabled(!isChecked);
        mTagValueEt.setHint(isChecked ? R.string.filter_tag_hint_disabled : R.string.filter_tag_hint);
    }
}
