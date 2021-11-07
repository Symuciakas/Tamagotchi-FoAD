package com.newproject.tamagotchi_foad;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Players")
public class Player implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long uid;
    @ColumnInfo(name = "Name")
    private String playerName;
    @ColumnInfo(name = "Level")
    private int level;
    @ColumnInfo(name = "Experience")
    private int experience;
    @ColumnInfo(name = "Score")
    private int score;
    @ColumnInfo(name = "Play time")
    private int playTime;
    @ColumnInfo(name = "Given pat count")
    private int patsGiven;
    @ColumnInfo(name = "Fed food count")
    private int foodFed;

    /**
     * Constructors
     */
    public Player() {
        this.playerName = "NULL";
        this.level = 0;
        this.experience = 0;
        this.score = 0;
        this.patsGiven = 0;
        this.foodFed = 0;
        this.playTime = 0;
     }
    public Player(long uid, String playerName, int level, int experience, int score, int playTime, int petsGiven, int foodFed) {
         this.uid = uid;
         this.playerName = playerName;
         this.level = level;
         this.experience = experience;
         this.score = score;
         this.playTime = playTime;
         this.patsGiven = petsGiven;
         this.foodFed = foodFed;
     }
    public Player(long uid, String playerName) {
         this.uid = uid;
         this.playerName = playerName;
         this.level = 0;
         this.experience = 0;
         this.score = 0;
         this.playTime = 0;
         this.patsGiven = 0;
         this.foodFed = 0;
     }

    /**
     * Getters
     */
    public long getUid() {
        return uid;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getLevel() {
        return level;
    }
    public int getExperience() {
        return experience;
    }
    public int getScore() {
        return score;
    }
    public int getPlayTime() {
        return playTime;
    }
    public int getPatsGiven() {
        return patsGiven;
    }
    public int getFoodFed() {
        return foodFed;
    }



    /**
     * Setters
     */
    public void setUid(long uid) {
        this.uid = uid;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setExperience(int experience) {
        this.experience = experience;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setPatsGiven(int patsGiven) {
        this.patsGiven = patsGiven;
    }
    public void setFoodFed(int foodFed) {
        this.foodFed = foodFed;
    }
    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    /**
     *
     */
    public void levelUp() {
        this.level++;
        this.experience = 0;
    }
    public void addExperience(int exp) {
        experience = experience + exp;
        if(experience >= 100)
            levelUp();
    }
    public void tickPlayTime() {
        playTime++;
    }
    public void givePats() {
        patsGiven++;
    }
    public void fed() {
        foodFed++;
    }
}
