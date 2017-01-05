/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.List;

public class GetApplicationsResponse {

    @JsonProperty("applications")
    public List<BeaconControlApplication> applications;

    public void validate() throws IOException {
        for (BeaconControlApplication beaconControlApplication : applications) {
            if (!beaconControlApplication.isValid()) {
                throw new IOException(String.format("Application response %d invalid.", beaconControlApplication.id));
            }
        }
    }
}
