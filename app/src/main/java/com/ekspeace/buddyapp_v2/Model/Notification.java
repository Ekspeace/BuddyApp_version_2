package com.ekspeace.buddyapp_v2.Model;

import com.google.firebase.Timestamp;

public class Notification {
    private String uid, title, content;
    private Timestamp serverTimestamp;

    public Notification() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Timestamp serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

}
