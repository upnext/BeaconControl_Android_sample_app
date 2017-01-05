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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class LoginErrorDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new LoginErrorDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.login_screen_error_dialog_title)
                .setMessage(R.string.login_screen_error_dialog_message)
                .setPositiveButton(R.string.login_screen_error_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((LoginActivity) getActivity()).logIn();
                    }
                })
                .setNegativeButton(R.string.login_screen_error_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
}
