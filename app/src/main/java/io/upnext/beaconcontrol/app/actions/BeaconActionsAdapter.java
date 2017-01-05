/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.actions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.upnext.beaconcontrol.app.R;
import io.upnext.beaconcontrol.app.actions.BeaconActionsAdapter.ActionViewHolder;

public class BeaconActionsAdapter extends RecyclerView.Adapter<ActionViewHolder> {

    private final List<BeaconAction> actions = new ArrayList<>();

    public void setActions(List<BeaconAction> newActions) {
        actions.clear();
        actions.addAll(newActions);
        notifyDataSetChanged();
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_action_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, int position) {
        holder.populate(actions.get(position));
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    @Override
    public long getItemId(int position) {
        return actions.get(position).timestamp;
    }

    public static class ActionViewHolder extends RecyclerView.ViewHolder {
        private static final DateFormat ACTION_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        private TextView actionName;
        private TextView actionTimestamp;

        public ActionViewHolder(View itemView) {
            super(itemView);
            actionName = (TextView) itemView.findViewById(R.id.action_name);
            actionTimestamp = (TextView) itemView.findViewById(R.id.action_timestamp);
        }

        public void populate(BeaconAction action) {
            actionName.setText(action.actionName);
            actionTimestamp.setText(ACTION_DATE_FORMAT.format(new Date(action.timestamp)));
        }
    }
}