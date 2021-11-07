package com.newproject.tamagotchi_foad;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Calendar;
import java.util.Date;

/**
 * Pet class is for user Pet objects;
 * static data is received from PetData class;
 */

@Entity(tableName = "PlayerPets")
public class Pet extends PetData {
    @ColumnInfo(name = "Level")
    private int level;
    @ColumnInfo(name = "Experience")
    private int experience;
    @ColumnInfo(name = "Affection")
    private int affection;
    @ColumnInfo(name = "Health")
    private int health;
    @ColumnInfo(name = "Happiness")
    private int happiness;
    @ColumnInfo(name = "Saturation")
    private int saturation;

    /**
     * Constructors
     */
    public Pet() {

    }
    public Pet(PetData petData) {
        this.uid = petData.uid;
        this.name = petData.name;
        this.maxHealth = petData.maxHealth;
        this.healthLoss = petData.healthLoss;
        this.maxHappiness = petData.maxHappiness;
        this.happinessLoss = petData.happinessLoss;
        this.startingAffection = petData.startingAffection;
        this.affectionLoss = petData.affectionLoss;
        this.maxSaturation = petData.maxSaturation;
        this.saturationLoss = petData.saturationLoss;

        this.level = 0;
        this.experience = 0;
        this.health = petData.maxHealth;
        this.happiness = petData.maxHappiness;
        this.affection = petData.startingAffection;
        this.saturation = petData.maxSaturation;
    }

    /**
     * Getters
     */
    public int getLevel() {
        return level;
    }
    public int getExperience() {
        return experience;
    }
    public int getHealth() {
        return health;
    }
    public int getHappiness() {
        return happiness;
    }
    public int getAffection() {
        return affection;
    }
    public int getSaturation() {
        return saturation;
    }

    /**
     * Setters
     */
    public void setLevel(int level) {
        this.level = level;
    }
    public void setExperience(int experience) {
        this.experience = experience;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }
    public void setAffection(int affection) {
        this.affection = affection;
    }
    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    /**
     * Actions
     */
    public boolean Feed(Food food) {
        if(saturation != maxSaturation) {
            if(saturation + food.getValue() < maxSaturation)
                saturation = saturation + food.getValue();
            else
                saturation = maxSaturation;
            //affection++;
            return true;
        }
        else
            return false;
    }
    public void happinessUp() {
        //affection++;
        happiness++;
    }
}
