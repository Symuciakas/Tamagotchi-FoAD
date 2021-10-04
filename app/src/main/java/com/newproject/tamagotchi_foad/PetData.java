package com.newproject.tamagotchi_foad;

/**
 * PetData is a class for default pet data.
 * PetData should be read only.
 */

public class PetData {
    protected int id;
    protected String name;
    protected int maxHealth;
    protected int healthLoss;// 1 per value mSeconds
    protected int maxHappiness;
    protected int happinessLoss;// 1 per value mSeconds
    protected int startingAffection;
    protected int affectionLoss;// 1 per value mSeconds
    protected int maxSaturation;
    protected int saturationLoss;// 1 per value mSeconds

    /**
     * Constructors
     */

    public PetData() {

    }

    public PetData(int id, String name, int maxHealth, int healthLoss, int maxHappiness, int happinessLoss, int startingAffection, int affectionLoss, int maxSaturation, int saturationLoss) {
        this.id = id;
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

    public int getId() {
        return id;
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
}
