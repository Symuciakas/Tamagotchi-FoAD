package com.newproject.tamagotchi_foad;

public class Player {
    private int uid; //Might become string
    private String username;
    private String password;
    private String playerName;
    private int level;
    private int experience;
    private int score;
    private int petsGiven;
    private int foodFed;

    /**
     * Constructors
     */
    public Player() {

     }
    public Player(int uid,  String username, String password, String playerName, int level, int experience, int score, int petsGiven, int foodFed) {
         this.uid = uid;
         this.username = username;
         this.password = password;
         this.playerName = playerName;
         this.level = level;
         this.experience = experience;
         this.score = score;
         this.petsGiven = petsGiven;
         this.foodFed = foodFed;
     }
    public Player(int uid,  String username, String password, String playerName) {
         this.uid = uid;
         this.username = username;
         this.password = password;
         this.playerName = playerName;
         this.level = 0;
         this.experience = 0;
         this.score = 0;
         this.petsGiven = 0;
         this.foodFed = 0;
     }
    public Player(String username, String password) {
         //Generate?
        this.uid = -1;
        this.username = username;
        this.password = password;
        this.playerName = "NULL";
        this.level = 0;
        this.experience = 0;
        this.score = 0;
        this.petsGiven = 0;
        this.foodFed = 0;
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
    public String getPassword() {
        //Hash?
        return password;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getLevel() {
        return level;
    }
    public int getScore() {
        return score;
    }
    public int getPetsGiven() {
        return petsGiven;
    }
    public int getFoodFed() {
        return foodFed;
    }

    /**
     * Setters
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public void setScore(int score) {
        this.score = score;
    }

    /**
     *
     */
    public void levelUp() {
        this.level++;
    }
    public void givePats() {
        petsGiven++;
    }
    public void feed() {
        foodFed++;
    }
}
