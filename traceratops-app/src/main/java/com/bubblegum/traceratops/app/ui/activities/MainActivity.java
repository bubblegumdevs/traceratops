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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toolbar;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.ui.fragments.DebugFragment;
import com.bubblegum.traceratops.app.ui.fragments.LoggerFragment;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(getString(R.string.dash_board));
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentManager fm = getSupportFragmentManager();
        TraceratopsAppViewPagerFragmentAdapter adapter = new TraceratopsAppViewPagerFragmentAdapter(fm);

        DebugFragment debugFragment = new DebugFragment();
        LoggerFragment loggerFragment = new LoggerFragment();
        adapter.addFragment(debugFragment, getString(R.string.debug_fragment_title));
        adapter.addFragment(loggerFragment, getString(R.string.logger_fragment_title));

        viewPager.setAdapter(adapter);
    }

    private class TraceratopsAppViewPagerFragmentAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> fragmentTitles = new ArrayList<>();

        public TraceratopsAppViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= 0 && position < fragments.size()) {
                return fragments.get(position);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position < fragmentTitles.size()) {
                return fragmentTitles.get(position);
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
