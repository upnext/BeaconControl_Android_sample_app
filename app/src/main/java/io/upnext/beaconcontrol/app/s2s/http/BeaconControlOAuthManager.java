/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http;

import io.upnext.beaconcontrol.app.s2s.http.request.TokenRequest;
import io.upnext.beaconcontrol.app.s2s.http.response.TokenResponse;
import retrofit2.Call;

public class BeaconControlOAuthManager {

    private final String email;
    private final String password;
    private final IBeaconControlOAuthService oauthService;
    private final String clientSecret;
    private final String clientId;

    public BeaconControlOAuthManager(String email, String password, IBeaconControlOAuthService oauthService, String clientSecret, String clientId) {
        this.email = email;
        this.password = password;
        this.oauthService = oauthService;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
    }

    public Call<TokenResponse> getNewTokenCall() {
        TokenRequest tokenRequest = new TokenRequest(
                clientId,
                clientSecret,
                TokenRequest.GrantType.password,
                email,
                password
        );

        return oauthService.getToken(tokenRequest);
    }
}
