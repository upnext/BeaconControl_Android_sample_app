/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BeaconControlApplication implements Serializable {

    @JsonProperty("id")
    public long id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("uid")
    public String uid;

    @JsonProperty("secret")
    public String secret;

    @JsonProperty("test")
    public boolean test;

    public boolean isValid() {
        return name != null && !name.isEmpty() &&
                uid != null && !uid.isEmpty() &&
                secret != null && !secret.isEmpty();
    }
}
