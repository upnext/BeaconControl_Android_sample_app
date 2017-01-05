/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http;

import io.upnext.beaconcontrol.app.s2s.http.response.GetApplicationsResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IApplicationService {

    @GET("applications")
    Call<GetApplicationsResponse> getApplications();
}
