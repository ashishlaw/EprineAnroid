package com.eprine.retrofitclasses;

import com.eprine.ForgotPassword;
import com.eprine.propertyClasses.LoginProp;
import com.eprine.propertyClasses.forgotPassword.ForgotPasswordProp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login.json")
    Call<LoginProp> loginAPI(@Field("username") String username, @Field("password") String password, @Field("app_name") String app_name, @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("recoverpass.json")
    Call<ForgotPasswordProp> forgotPasswordAPI(@Field("username") String username);
}