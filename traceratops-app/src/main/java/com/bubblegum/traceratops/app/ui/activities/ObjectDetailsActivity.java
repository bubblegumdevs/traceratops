package com.bubblegum.traceratops.app.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.ui.fragments.ObjectDetailsFragment;

public class ObjectDetailsActivity extends BaseActivity {

    public static final String EXTRA_TLOG_OBJECT = ":tlog:object";
    public static final String EXTRA_TLOG_TAG = ":tlog:tag";
    public static final String EXTRA_TLOG_DESCRIPTION = ":tlog:desc";
    public static final String TAG_OBJECT_DETAIL_FRAGMENT = ":tlog:object:fragment";


    ObjectDetailsFragment fragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragment = new ObjectDetailsFragment();
        Bundle args = getIntent().getBundleExtra(EXTRA_TLOG_OBJECT);
        String tag = getIntent().getStringExtra(EXTRA_TLOG_DESCRIPTION);
        if(tag!=null) {
            getSupportActionBar().setTitle(tag);
        }
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, TAG_OBJECT_DETAIL_FRAGMENT).commit();
    }
}
