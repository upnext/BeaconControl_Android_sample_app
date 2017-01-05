/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.beacons;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.upnext.beaconcontrol.app.R;
import io.upnext.beaconcontrol.app.beacons.BeaconsAdapter.BeaconViewHolder;
import io.upnext.beaconcontrol.sdk.Beacon;
import io.upnext.beaconcontrol.sdk.Beacon.Proximity;

public class BeaconsAdapter extends RecyclerView.Adapter<BeaconViewHolder> {

    private static final String TAG = "BeaconsAdapter";
    private static final int NOT_FOUND = -1;

    private final List<Beacon> beacons = new ArrayList<>();
    private final BeaconComparator beaconComparator = new BeaconComparator();

    public void setItems(List<Beacon> newBeacons) {
        beacons.clear();
        beacons.addAll(newBeacons);
        Collections.sort(beacons, beaconComparator);
        notifyDataSetChanged();
    }

    public void updateItem(Beacon updatedBeacon) {
        final int oldPos = getPositionInList(updatedBeacon);
        if (oldPos == NOT_FOUND) {
            Log.d(TAG, "Could not find beacond with id " + updatedBeacon.id);
            return;
        }

        final Beacon oldBeacon = beacons.get(oldPos);
        oldBeacon.currentProximity = updatedBeacon.currentProximity;
        oldBeacon.distance = updatedBeacon.distance;

        Collections.sort(beacons, beaconComparator);
        final int newPos = getPositionInList(updatedBeacon);
        notifyItemChanged(oldPos);
        notifyItemChanged(newPos);
    }

    private int getPositionInList(Beacon beaconToFind) {
        for (int i = 0; i < beacons.size(); i++) {
            if (BeaconComparator.areEqual(beaconToFind, beacons.get(i))) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeaconViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        holder.populate(beacons.get(position));
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    @Override
    public long getItemId(int position) {
        return beacons.get(position).id;
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder {
        private TextView beaconName;
        private TextView beaconProximity;
        private TextView beaconUUID;
        private TextView beaconDistance;

        public BeaconViewHolder(View itemView) {
            super(itemView);
            beaconName = (TextView) itemView.findViewById(R.id.beacon_name);
            beaconProximity = (TextView) itemView.findViewById(R.id.beacon_proximity);
            beaconUUID = (TextView) itemView.findViewById(R.id.beacon_uuid);
            beaconDistance = (TextView) itemView.findViewById(R.id.beacon_distance);
        }

        public void populate(Beacon beacon) {
            final boolean isInRange = beacon.currentProximity != Proximity.OUT_OF_RANGE;
            beaconName.setText(beacon.name);
            beaconProximity.setText(beacon.currentProximity.name());
            beaconUUID.setText(beacon.uuid);

            beaconName.setEnabled(isInRange);
            beaconProximity.setEnabled(isInRange);
            beaconUUID.setEnabled(isInRange);

            setDistance(beacon.distance, isInRange);
        }

        private void setDistance(double distance, boolean isInRange) {
            //special case when beacon appeared (just came to FAR proximity), but we do not know the exact distance yet
            if (isInRange && distance != Beacon.DISTANCE_UNDEFINED) {
                beaconDistance.setVisibility(View.VISIBLE);
                beaconDistance.setText(String.format(Locale.getDefault(), "%.2fm", distance));
            } else {
                beaconDistance.setVisibility(View.GONE);
            }
        }
    }
}