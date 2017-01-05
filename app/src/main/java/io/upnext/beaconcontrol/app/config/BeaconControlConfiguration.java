/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.config;

import android.content.Context;
import android.content.SharedPreferences;

public class BeaconControlConfiguration {

    private static final String SHARED_PREFERENCES_KEY = "io.upnext.beaconcontrol.sampleapp.settings.CONFIGURATION";
    private static final String PREFERENCE_KEY_CLIENT_ID = "client_id";
    private static final String PREFERENCE_KEY_CLIENT_SECRET = "client_secret";
    private static final String PREFERENCE_KEY_USER_ID = "user_id";

    private static final String DEFAULT_USER_ID = "sampleapp_user";

    private final SharedPreferences preferences;

    public BeaconControlConfiguration(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void setClientId(String clientId) {
        preferences.edit().putString(PREFERENCE_KEY_CLIENT_ID, clientId).apply();
    }

    public String getClientId() {
        if (!preferences.contains(PREFERENCE_KEY_CLIENT_ID)) {
            throw new IllegalStateException("Client id requested, but unavailable in preferences.");
        }

        return preferences.getString(PREFERENCE_KEY_CLIENT_ID, "");
    }

    public void setClientSecret(String clientSecret) {
        preferences.edit().putString(PREFERENCE_KEY_CLIENT_SECRET, clientSecret).apply();
    }

    public String getClientSecret() {
        if (!preferences.contains(PREFERENCE_KEY_CLIENT_SECRET)) {
            throw new IllegalStateException("Client secret requested, but unavailable in preferences.");
        }

        return preferences.getString(PREFERENCE_KEY_CLIENT_SECRET, "");
    }

    public boolean isLoggedIn() {
        return preferences.contains(PREFERENCE_KEY_CLIENT_SECRET);
    }

    public String getUserId() {
        return preferences.getString(PREFERENCE_KEY_USER_ID, DEFAULT_USER_ID);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}