package com.eprine.retrofitclasses;

import com.eprine.ForgotPassword;
import com.eprine.propertyClasses.CheckLogin;
import com.eprine.propertyClasses.LoginProp;
import com.eprine.propertyClasses.forgotPassword.ForgotPasswordProp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login.json")
    Call<LoginProp> loginAPI(@Field("username") String username, @Field("password") String password, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("recoverpass.json")
    Call<ForgotPasswordProp> forgotPasswordAPI(@Field("username") String username);

    @FormUrlEncoded
    @POST("checkLogin.json")
    Call<CheckLogin> checkLoginAPI(@Field("username") String username, @Field("password") String password);
}
