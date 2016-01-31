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

package com.bubblegum.traceratops.app.ui.activities;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bubblegum.traceratops.app.ui.Snackable;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_INDEFINITE = 3;

    @SuppressWarnings("ResourceType") // case is handled
    public  @Nullable Snackbar showSnackbarOrToast(String message, int length, String actionLabel, View.OnClickListener action) {
        if (this instanceof Snackable) {
            return ((Snackable) this).showSnackbar(message, getActualLength(length, true), actionLabel, action);
        } else {
            Toast.makeText(this, message, getActualLength(length, false)).show();
            return null;
        }
    }

    public  @Nullable Snackbar showSnackbarOrToast(@StringRes int stringResId, int length, String actionLabel, View.OnClickListener action) {
        return showSnackbarOrToast(getString(stringResId), length, actionLabel, action);
    }

    private int getActualLength(int length, boolean isSnackbar) {
        switch (length) {
            case LENGTH_SHORT:
                return isSnackbar ? Snackbar.LENGTH_SHORT : Toast.LENGTH_SHORT;
            case LENGTH_INDEFINITE:
                return isSnackbar ? Snackbar.LENGTH_INDEFINITE : Toast.LENGTH_LONG;
            default:
            case LENGTH_LONG:
                return isSnackbar ? Snackbar.LENGTH_LONG : Toast.LENGTH_LONG;
        }
    }
}
