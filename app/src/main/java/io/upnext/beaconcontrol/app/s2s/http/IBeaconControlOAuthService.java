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
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IBeaconControlOAuthService {

    @POST("oauth/token")
    Call<TokenResponse> getToken(@Body TokenRequest tokenRequest);
}
