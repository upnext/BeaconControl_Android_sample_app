/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.upnext.beaconcontrol.app.config.BeaconControlConfiguration;

public class StartupActivity extends Activity {

    public static Intent getIntent(Context context) {
        return new Intent(context, StartupActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeaconControlConfiguration beaconControlConfiguration = new BeaconControlConfiguration(this);

        if (beaconControlConfiguration.isLoggedIn()) {
            startActivity(MainActivity.getIntent(this));
        } else {
            startActivity(LoginActivity.getIntent(this));
        }
    }
}
