package com.newproject.tamagotchi_foad;

public class User {
    private int uid; //Might become string
    private String username;
    private Player playerData;

    /**
     * Constructors
     */

    public User() {

    }

    public User(int uid, String username, Player playerData) {
        this.uid = uid;
        this.username = username;
        this.playerData = playerData;
    }

    /**
     * Getters
     */

    public int getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public Player getPlayerData() {
        return playerData;
    }
}
