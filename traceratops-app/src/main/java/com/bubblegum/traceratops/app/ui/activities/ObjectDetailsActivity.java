package com.bubblegum.traceratops.app.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.ui.fragments.ObjectDetailsFragment;

public class ObjectDetailsActivity extends BaseActivity {

    public static final String EXTRA_TLOG_OBJECT = ":tlog:object";
    public static final String TAG_OBJECT_DETAIL_FRAGMENT = ":tlog:object:fragment";


    ObjectDetailsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_details);
        fragment = new ObjectDetailsFragment();
        Bundle args = getIntent().getBundleExtra(EXTRA_TLOG_OBJECT);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, TAG_OBJECT_DETAIL_FRAGMENT).commit();
    }
}
