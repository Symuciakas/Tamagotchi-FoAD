package com.newproject.tamagotchi_foad;

public class Player {
    private String playerName;
    private int level;

    /**
     * Constructors
     */

     public Player() {

     }

     public Player(String playerName, int level) {
         this.playerName = playerName;
         this.level = level;
     }

     public Player(String databaseReference) {
         //Not implemented
     }

    /**
     * Getters
     */

    public String getPlayerName() {
        return playerName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Setters
     */

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     *
     */

    public  void levelUp() {
        this.level++;
    }
}
