package com.eprine.propertyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails {
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("full_name")
    @Expose
    private String full_name;
    @SerializedName("role_name")
    @Expose
    private String role_name;
    @SerializedName("unconfirmed")
    @Expose
    private Long unconfirmed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public Long getUnconfirmed() {
        return unconfirmed;
    }

    public void setUnconfirmed(Long unconfirmed) {
        this.unconfirmed = unconfirmed;
    }
}
