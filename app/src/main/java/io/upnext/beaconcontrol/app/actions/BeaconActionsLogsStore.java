/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.upnext.beaconcontrol.sdk.Action;
import io.upnext.beaconcontrol.sdk.ErrorCode;

public class BeaconActionsLogsStore {

    private static final String SHARED_PREFERENCES_KEY = "io.upnext.beaconcontrol.sampleapp.logs.LOGS_STORE";
    private static final String PREFERENCE_LOGS = "PREFERENCE_LOGS";
    private static final String ENTRY_SEPARATOR = "§";
    private static final String VALUE_SEPARATOR = "~";
    private static final String EMPTY_STRING = "";

    private static final int ACTION_NAME_INDEX = 0;
    private static final int ACTION_TIMESTAMP_INDEX = 1;

    private static final String ERROR_PREFIX = "ERROR ";

    interface ActionsListener {
        void onActionAdded();
    }

    private final SharedPreferences preferences;
    private final OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private ActionsListener listener;

    public BeaconActionsLogsStore(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (listener != null) {
                    listener.onActionAdded();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void setListener(ActionsListener listener) {
        this.listener = listener;
    }

    public void logActionStart(Action action) {
        writeLog(action.name);
    }

    public void logError(ErrorCode errorCode) {
        writeLog(ERROR_PREFIX + errorCode);
    }

    //TODO optimize to have in-memory list
    public void writeLog(String logEntry) {
        BeaconAction beaconAction = new BeaconAction(logEntry, System.currentTimeMillis());

        LinkedList<BeaconAction> logsList = new LinkedList<>();
        logsList.addAll(getLogs());

        //add new entry at the beginning
        logsList.add(0, beaconAction);

        StringBuilder stringBuilder = new StringBuilder();
        for (BeaconAction action : logsList) {
            stringBuilder
                    .append(action.actionName).append(VALUE_SEPARATOR)
                    .append(action.timestamp)
                    .append(ENTRY_SEPARATOR);
        }
        //remove last separator
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length() - 1, EMPTY_STRING);

        preferences.edit().putString(PREFERENCE_LOGS, stringBuilder.toString()).apply();
    }

    private List<BeaconAction> fromStringArray(String[] actions) {
        List<BeaconAction> actionList = new ArrayList<>(actions.length);
        for (String action : actions) {
            String[] values = action.split(VALUE_SEPARATOR);
            actionList.add(new BeaconAction(values[ACTION_NAME_INDEX], Long.valueOf(values[ACTION_TIMESTAMP_INDEX])));
        }
        return actionList;
    }

    public List<BeaconAction> getLogs() {
        String currentLogs = preferences.getString(PREFERENCE_LOGS, EMPTY_STRING);
        if (currentLogs.isEmpty()) {
            return new ArrayList<>();
        }
        return fromStringArray(currentLogs.split(ENTRY_SEPARATOR));
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}