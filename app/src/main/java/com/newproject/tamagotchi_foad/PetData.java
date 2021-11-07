package com.newproject.tamagotchi_foad;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * PetData is a class for default pet data.
 * PetData should be read only.
 */

@Entity(tableName = "Pets")
public class PetData {
    @PrimaryKey(autoGenerate = true)
    protected long uid;
    @ColumnInfo(name = "Name")
    protected String name;
    @ColumnInfo(name = "Max health")
    protected int maxHealth;
    @ColumnInfo(name = "Health loss")
    protected int healthLoss;// 1 per value mSeconds
    @ColumnInfo(name = "Max happiness")
    protected int maxHappiness;
    @ColumnInfo(name = "Happiness loss")
    protected int happinessLoss;// 1 per value mSeconds
    @ColumnInfo(name = "Starting affection")
    protected int startingAffection;
    @ColumnInfo(name = "Affection loss")
    protected int affectionLoss;// 1 per value mSeconds
    @ColumnInfo(name = "Max saturation")
    protected int maxSaturation;
    @ColumnInfo(name = "Saturation loss")
    protected int saturationLoss;// 1 per value mSeconds

    /**
     * Constructors
     */

    public PetData() {
        this.name = "NULL";
        this.maxHealth = 1;
        this.healthLoss = -1;
        this.maxHappiness = 1;
        this.happinessLoss = -1;
        this.startingAffection = 1;
        this.affectionLoss = -1;
        this.maxSaturation = 1;
        this.saturationLoss = -1;
    }

    public PetData(long uid, String name, int maxHealth, int healthLoss, int maxHappiness, int happinessLoss, int startingAffection, int affectionLoss, int maxSaturation, int saturationLoss) {
        this.uid = uid;
        this.name = name;
        this.maxHealth = maxHealth;
        this.healthLoss = healthLoss;
        this.maxHappiness = maxHappiness;
        this.happinessLoss = happinessLoss;
        this.startingAffection = startingAffection;
        this.affectionLoss = affectionLoss;
        this.maxSaturation = maxSaturation;
        this.saturationLoss = saturationLoss;
    }

    /**
     * Getters
     */
    public long getUid() {
        return uid;
    }
    public String getName() {
        return name;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getHealthLoss() {
        return healthLoss;
    }
    public int getMaxHappiness() {
        return maxHappiness;
    }
    public int getHappinessLoss() {
        return happinessLoss;
    }
    public int getStartingAffection() {
        return startingAffection;
    }
    public int getAffectionLoss() {
        return affectionLoss;
    }
    public int getMaxSaturation() {
        return maxSaturation;
    }
    public int getSaturationLoss() {
        return saturationLoss;
    }

    /**
     * Setters
     */
    public void setId(long uid) {
        this.uid = uid;
    }
    public void setAffectionLoss(int affectionLoss) {
        this.affectionLoss = affectionLoss;
    }
    public void setHappinessLoss(int happinessLoss) {
        this.happinessLoss = happinessLoss;
    }
    public void setHealthLoss(int healthLoss) {
        this.healthLoss = healthLoss;
    }
    public void setMaxHappiness(int maxHappiness) {
        this.maxHappiness = maxHappiness;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void setMaxSaturation(int maxSaturation) {
        this.maxSaturation = maxSaturation;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSaturationLoss(int saturationLoss) {
        this.saturationLoss = saturationLoss;
    }
    public void setStartingAffection(int startingAffection) {
        this.startingAffection = startingAffection;
    }
}
