package com.eprine.propertyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginData {
    @SerializedName("users")
    @Expose
    private List<UserDetails> users;
    public List<UserDetails> getUsers() {
        return users;
    }

    public void setUsers(List<UserDetails> users) {
        this.users = users;
    }
}
