/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class LocationPermissionExplainingDialogFragment extends DialogFragment {

    public static final String URI_SCHEME_PACKAGE = "package";

    public static DialogFragment newInstance() {
        return new LocationPermissionExplainingDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_location_permission_required_title)
                .setMessage(R.string.dialog_location_permission_required_message)
                .setPositiveButton(R.string.dialog_location_permission_required_button_positive, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showApplicationSettings();
                    }
                })
                .setNegativeButton(R.string.dialog_location_permission_required_button_negative, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                })
                .create();
    }

    private void showApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(URI_SCHEME_PACKAGE, getContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}