/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.upnext.beaconcontrol.app.config.BeaconControlConfiguration;
import io.upnext.beaconcontrol.app.s2s.http.BeaconControlManager;
import io.upnext.beaconcontrol.app.s2s.http.HttpListener;
import io.upnext.beaconcontrol.app.s2s.http.mediator.GetApplicationsMediator;
import io.upnext.beaconcontrol.app.s2s.http.mediator.LoginCallMediator;
import io.upnext.beaconcontrol.app.s2s.http.model.ErrorCode;
import io.upnext.beaconcontrol.app.s2s.http.response.BeaconControlApplication;
import io.upnext.beaconcontrol.app.s2s.http.response.GetApplicationsResponse;
import io.upnext.beaconcontrol.app.s2s.http.response.TokenResponse;

public class LoginActivity extends FragmentActivity {

    private static final String TAG = "LoginActivity";

    private static final String CREATE_ACCOUNT_DIALOG_TAG = "CREATE_ACCOUNT_DIALOG_TAG";
    private static final String LOGIN_ERROR_DIALOG_TAG = "LOGIN_ERROR_DIALOG_TAG";

    public static Intent getIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    private BeaconControlManager beaconControlManager;
    private LoginCallMediator loginCallMediator;
    private GetApplicationsMediator getApplicationsMediator;
    private BeaconControlConfiguration beaconControlConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        wireViews();

        beaconControlConfiguration = new BeaconControlConfiguration(LoginActivity.this);
    }

    private void wireViews() {
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logIn();
                }
                return false;
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Button loginButton = (Button) findViewById(R.id.login_btn);
        loginButton.setOnClickListener(loginButtonListener);

        TextView createAccountLink = (TextView) findViewById(R.id.create_account);
        createAccountLink.setPaintFlags(createAccountLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        createAccountLink.setOnClickListener(createAccountListener);
    }

    private final View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logIn();
        }
    };

    private final View.OnClickListener createAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showCreateAccountDialog();
        }
    };

    /*package*/
    void logIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        beaconControlManager = new BeaconControlManager(email, password);
        loginCallMediator = new LoginCallMediator(LoginActivity.this, beaconControlManager, new HttpListener<TokenResponse>() {
            @Override
            public void onStart() {
                showBusy();
            }

            @Override
            public void onSuccess(TokenResponse response) {
                if (response == null || response.accessToken == null || response.tokenType == null) {
                    showIdle();
                    showLoginErrorDialog();
                    Log.e(TAG, "corrupted token returned by oauth endpoint.");
                } else {
                    beaconControlManager.setToken(response);
                    getClientInfoFromTestApplication();
                }
            }

            @Override
            public void onError(ErrorCode errorCode) {
                showIdle();
                showLoginErrorDialog();
                Log.e(TAG, "error occurred during login: " + errorCode);
            }

            @Override
            public void onEnd() {
                // ignore
            }
        });
        loginCallMediator.logIn();
    }

    private void getClientInfoFromTestApplication() {
        getApplicationsMediator = new GetApplicationsMediator(this, beaconControlManager, new HttpListener<GetApplicationsResponse>() {

            @Override
            public void onStart() {
                // ignore
            }

            @Override
            public void onSuccess(GetApplicationsResponse response) {
                for (BeaconControlApplication application : response.applications) {
                    if (application.test) {
                        beaconControlConfiguration.setClientId(application.uid);
                        beaconControlConfiguration.setClientSecret(application.secret);
                        break;
                    }
                }
                startActivity(MainActivity.getIntent(LoginActivity.this));
            }

            @Override
            public void onError(ErrorCode errorCode) {
                showLoginErrorDialog();
                Log.e(TAG,  "error occurred during getApplications request: " + errorCode);
            }

            @Override
            public void onEnd() {
                showIdle();
            }
        });
        getApplicationsMediator.getApplications();
    }

    private void showBusy() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showIdle() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showCreateAccountDialog() {
        DialogFragment dialogFragment = CreateAccountDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), CREATE_ACCOUNT_DIALOG_TAG);
    }

    private void showLoginErrorDialog() {
        DialogFragment dialogFragment = LoginErrorDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), LOGIN_ERROR_DIALOG_TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (loginCallMediator != null) {
            loginCallMediator.cancel();
        }
        if (getApplicationsMediator != null) {
            getApplicationsMediator.cancel();
        }
    }
}
