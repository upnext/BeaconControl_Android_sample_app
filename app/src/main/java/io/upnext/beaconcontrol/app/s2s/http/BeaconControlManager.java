/*
 * Copyright (c) 2016, Upnext Technologies Sp. z o.o.
 * All rights reserved.
 *
 * This source code is licensed under the BSD 3-Clause License found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package io.upnext.beaconcontrol.app.s2s.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.upnext.beaconcontrol.app.s2s.http.response.GetApplicationsResponse;
import io.upnext.beaconcontrol.app.s2s.http.response.TokenResponse;
import io.upnext.beaconcontrol.sdk.backend.BeaconControlTokenService;
import io.upnext.beaconcontrol.sdk.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BeaconControlManager {

    private static final String SERVICE_BASE_URL = "https://admin.beaconcontrol.io/s2s_api/v1/";
    private static final String CLIENT_SECRET = "55f560c85ce5e645c928eb72537a1183b87b947d9d4129a8786c10f6bff3613b";
    private static final String CLIENT_ID = "76b8780413c3902d76ae7a05b9a17dcb04ed0696147696d3b4ff3302269efc32";

    private static final int CONNECTION_TIMEOUT_IN_SECONDS = 15;
    private static final int READ_TIMEOUT_IN_SECONDS = 20;
    private static final int WRITE_TIMEOUT_IN_SECONDS = 20;

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_ACCEPT_VALUE_JSON = "application/json";

    private final BeaconControlOAuthManager beaconControlOAuthManager;
    private final IApplicationService applicationsControlService;
    private TokenResponse token;

    public BeaconControlManager(String email, String password) {
        ObjectMapper objectMapper = getObjectMapper();

        this.beaconControlOAuthManager = new BeaconControlOAuthManager(email, password, createService(getRetrofitInstance(SERVICE_BASE_URL, objectMapper, getHttpClient(false)), IBeaconControlOAuthService.class), CLIENT_SECRET, CLIENT_ID);
        this.applicationsControlService = createService(getRetrofitInstance(SERVICE_BASE_URL, objectMapper, getHttpClient(true)), IApplicationService.class);
    }

    public void setToken(TokenResponse token) {
        this.token = token;
    }

    private <T> T createService(Retrofit retrofit, Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    private Retrofit getRetrofitInstance(String serviceBaseUrl, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(serviceBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    public Call<TokenResponse> logIn() {
        return beaconControlOAuthManager.getNewTokenCall();
    }

    public Call<GetApplicationsResponse> getApplications() {
        return applicationsControlService.getApplications();
    }

    private OkHttpClient getHttpClient(boolean tokenAuthorizationReqiured) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .addNetworkInterceptor(new AcceptHeaderInterceptor());
        if (tokenAuthorizationReqiured) {
            builder.addNetworkInterceptor(new AuthenticatingInterceptor());
        }
        return builder.build();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }

    private class AuthenticatingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(getRequestWithHeaders(chain.request()));
        }

        private Request getRequestWithHeaders(Request originalRequest) {
            return originalRequest.newBuilder()
                    .addHeader(BeaconControlTokenService.HEADER_AUTHORIZATION, StringUtils.capitalizeFirstLetter(token.tokenType) + " " + token.accessToken)
                    .build();
        }
    }

    private class AcceptHeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(getRequestWithHeaders(chain.request()));
        }

        private Request getRequestWithHeaders(Request originalRequest) {
            return originalRequest.newBuilder()
                    .addHeader(HEADER_ACCEPT, HEADER_ACCEPT_VALUE_JSON)
                    .build();
        }
    }
}
