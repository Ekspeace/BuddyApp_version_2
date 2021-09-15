package com.ekspeace.buddyapp_v2.Model;

public class User {

    private String name;
    private String phone;
    private String email;
    private String password;
    private String playerId;
    private String id;

    public User() {
    }

    public User(String name, String phone, String email, String password, String playerId, String id) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.playerId = playerId;
        id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
