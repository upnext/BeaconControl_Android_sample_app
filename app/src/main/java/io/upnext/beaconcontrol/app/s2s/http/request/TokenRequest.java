/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequest extends BaseTokenRequest {

    @JsonProperty("email")
    public String email;

    @JsonProperty("password")
    public String password;

    public TokenRequest(String clientId, String clientSecret, GrantType grantType, String email, String password) {
        super(clientId, clientSecret, grantType);
        this.email = email;
        this.password = password;
    }
}