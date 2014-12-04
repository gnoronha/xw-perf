package com.collabora.xwperf.notxw_social;

import android.text.Html;
import android.text.Spanned;

import java.util.Date;

public class TweetModel {
    private UserModel userModel;
    private Spanned message;
    private Date timestamp;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public Spanned getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = Html.fromHtml(message);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
