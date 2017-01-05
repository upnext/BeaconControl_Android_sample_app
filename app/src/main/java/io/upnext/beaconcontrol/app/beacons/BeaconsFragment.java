/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.beacons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import io.upnext.beaconcontrol.app.R;
import io.upnext.beaconcontrol.sdk.Beacon;

public class BeaconsFragment extends Fragment {

    public interface ReloadConfigurationCallback {
        void reloadConfiguration();
    }

    public static BeaconsFragment getInstance() {
        return new BeaconsFragment();
    }

    private RecyclerView recyclerView;
    private View emptyView;
    private View loadingView;
    private Button reloadConfigurationButton;
    private BeaconsAdapter adapter;

    public void setBeacons(List<Beacon> beacons) {
        if (beacons == null || beacons.isEmpty()) {
            showEmptyView();
        } else {
            showList();
            adapter.setItems(beacons);
        }
    }

    public void onBeaconProximityChanged(Beacon beacon) {
        adapter.updateItem(beacon);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacons, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        loadingView = view.findViewById(R.id.loading_view);
        reloadConfigurationButton = (Button) view.findViewById(R.id.reload_configuration_button);
        reloadConfigurationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingView();
                ((ReloadConfigurationCallback) getActivity()).reloadConfiguration();
            }
        });

        setupRecycler();
        showLoadingView();

        return view;
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new BeaconsAdapter();
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    private void showList() {
        loadingView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        reloadConfigurationButton.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        loadingView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        reloadConfigurationButton.setVisibility(View.VISIBLE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        reloadConfigurationButton.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        emptyView = null;
        loadingView = null;
        reloadConfigurationButton = null;
    }
}