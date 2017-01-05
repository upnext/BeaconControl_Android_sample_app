/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.actions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.upnext.beaconcontrol.app.R;
import io.upnext.beaconcontrol.app.actions.BeaconActionsLogsStore.ActionsListener;

public class BeaconActionsFragment extends Fragment implements ActionsListener {

    public static BeaconActionsFragment getInstance() {
        return new BeaconActionsFragment();
    }

    private RecyclerView recyclerView;
    private View emptyView;
    private BeaconActionsLogsStore actionsLogsStore;
    private BeaconActionsAdapter actionsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actions, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        setupActionsLogsStore();
        setupRecycler();
        loadActions();

        return view;
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        actionsAdapter = new BeaconActionsAdapter();
        actionsAdapter.setHasStableIds(true);
        recyclerView.setAdapter(actionsAdapter);
    }

    private void setupActionsLogsStore() {
        actionsLogsStore = new BeaconActionsLogsStore(getContext());
        actionsLogsStore.setListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        emptyView = null;
    }

    @Override
    public void onActionAdded() {
        if (isVisible()) {
            loadActions();
        }
    }

    private void loadActions() {
        List<BeaconAction> actions = actionsLogsStore.getLogs();
        if (actions == null || actions.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            actionsAdapter.setActions(actions);
        }
    }
}