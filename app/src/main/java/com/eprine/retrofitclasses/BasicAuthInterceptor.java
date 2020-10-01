package com.eprine.retrofitclasses;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {
    private Context context;
    private int value;

    BasicAuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = null;
        if (value == 0) {
            authenticatedRequest = request.newBuilder()
                    .header("contentType", "application/json")
                    .build();
        }
        return chain.proceed(authenticatedRequest);
    }

}