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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bubblegum.traceratops.app.R;
import com.bubblegum.traceratops.app.ui.adapters.ObjectDetailAdapter;

public class ObjectDetailsFragment extends BaseFragment {

    RecyclerView mRecyclerView;
    ObjectDetailAdapter objectDetailAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_object_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.object_details_recycler_view);
        refresh();
    }

    public void refresh() {
        if(getArguments()!=null && mRecyclerView!=null) {
            objectDetailAdapter = new ObjectDetailAdapter(getContext(), getArguments());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(llm);
            mRecyclerView.setAdapter(objectDetailAdapter);
        }
    }
}
