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

package com.bubblegum.traceratops.app.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import com.bubblegum.traceratops.app.R;

public class AddDebugPreferenceDialogFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener {

    EditText tvKeyName;
    EditText tvKeyValue;
    Switch swBoolean;
    RadioButton rbString, rbBoolean;

    boolean isBooleanPicked;

    public static final String DEBUG_ARG_EDIT_MODE = "AddDebugPreferenceDialogFragment:EditMode";
    public static final String DEBUG_KEY_NAME = "AddDebugPreferenceDialogFragment:KeyName";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Add new preference")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addPreference();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_new_debug_preference, null);

        tvKeyName = (EditText) layout.findViewById(R.id.debug_key);
        tvKeyValue = (EditText) layout.findViewById(R.id.debug_value_string);
        swBoolean = (Switch) layout.findViewById(R.id.debug_value_boolean);
        rbBoolean = (RadioButton) layout.findViewById(R.id.radio_debug_boolean);
        rbString = (RadioButton) layout.findViewById(R.id.radio_debug_string);

        if (getArguments() != null) {
            boolean isEditMode = getArguments().getBoolean(DEBUG_ARG_EDIT_MODE, false);
            final String keyName = getArguments().getString(DEBUG_KEY_NAME, null);
            if (keyName == null) {
                isEditMode = false;
            }
            if (isEditMode) {
                tvKeyName.setText(keyName);
                tvKeyName.setEnabled(false);
                rbBoolean.setVisibility(View.GONE);
                rbString.setVisibility(View.GONE);

                builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.remove(keyName);
                    }
                });
            }
        }

        rbString.setOnCheckedChangeListener(this);
        rbBoolean.setOnCheckedChangeListener(this);

        builder.setView(layout);

        return builder.create();
    }

    private void addPreference() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        if(isBooleanPicked) {
            editor.putBoolean(tvKeyName.getText().toString(), swBoolean.isChecked());
        } else {
            editor.putString(tvKeyName.getText().toString(), tvKeyValue.getText().toString());
        }
        editor.apply();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == rbString && isChecked) {
            tvKeyValue.setVisibility(View.VISIBLE);
            swBoolean.setVisibility(View.GONE);
            isBooleanPicked = false;
        }
        if(buttonView == rbBoolean && isChecked) {
            tvKeyValue.setVisibility(View.GONE);
            swBoolean.setVisibility(View.VISIBLE);
            isBooleanPicked = true;
        }
    }
}
