/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http.mediator;

import android.content.Context;

import io.upnext.beaconcontrol.app.s2s.http.BeaconControlCallback;
import io.upnext.beaconcontrol.app.s2s.http.HttpListener;
import io.upnext.beaconcontrol.app.s2s.http.BeaconControlManager;
import io.upnext.beaconcontrol.app.s2s.http.model.ErrorCode;
import io.upnext.beaconcontrol.app.s2s.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Response;

public abstract class HttpCallMediator<R> {

    private final Context context;
    private final HttpListener listener;
    protected final BeaconControlManager beaconControlManager;

    protected Call<R> call;
    private boolean callCancelled;

    protected HttpCallMediator(Context context, BeaconControlManager beaconControlManager, HttpListener listener) {
        this.context = context;
        this.beaconControlManager = beaconControlManager;
        this.listener = listener;
    }

    private synchronized void setCallCancelled(boolean callCancelled) {
        this.callCancelled = callCancelled;
    }

    private synchronized boolean getCallCancelled() {
        return callCancelled;
    }

    private boolean callNotCancelledAndListenerPresent() {
        return !getCallCancelled() && listener != null;
    }

    private void notifyOnStart() {
        if (callNotCancelledAndListenerPresent()) {
            listener.onStart();
        }
    }

    private void notifyOnSuccess(R response) {
        if (callNotCancelledAndListenerPresent()) {
            listener.onSuccess(response);
        }
    }

    private void notifyOnError(ErrorCode errorCode) {
        if (callNotCancelledAndListenerPresent()) {
            listener.onError(errorCode);
        }
    }

    private void notifyOnEnd() {
        call = null;
        if (callNotCancelledAndListenerPresent()) {
            listener.onEnd();
        }
    }

    protected void onStartCall() {
        if (!NetworkUtils.isOnline(context)) {
            notifyOnError(ErrorCode.OFFLINE);
            return;
        }

        setCallCancelled(false);

        notifyOnStart();
        execute();
        enqueueCall(call);
    }

    private void enqueueCall(Call<R> call) {
        call.enqueue(new BeaconControlCallback<R>() {
            @Override
            protected void onSuccess(R response) {
                notifyOnEnd();
                notifyOnSuccess(response);
            }

            @Override
            protected void onError(Response<R> response, Throwable t) {
                notifyOnEnd();
                notifyOnError(getErrorCode(response, t));
            }
        });
    }

    private ErrorCode getErrorCode(Response<R> response, Throwable t) {
        // TODO add better error notification
        if (response == null) {
            return ErrorCode.IO_ERROR;
        } else {
            return ErrorCode.BEACON_CONTROL_ERROR;
        }
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
            setCallCancelled(true);
        }
    }

    protected abstract void execute();
}
