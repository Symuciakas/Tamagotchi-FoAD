package com.newproject.tamagotchi_foad;

/**
 * PetData is a class for default pet data.
 * PetData should be read only.
 */

public class PetData {
    protected int id;
    protected String name;
    protected int maxHealth;
    protected int maxHappiness;
    protected int startingAffection;
    protected int maxSaturation;

    /**
     * Constructors
     */

    public PetData() {

    }

    public PetData(int id, String name, int maxHealth, int maxHappiness, int startingAffection, int maxSaturation) {
        this.id = id;
        this.name = name;
        this.maxHealth = maxHealth;
        this.maxHappiness = maxHappiness;
        this.startingAffection = startingAffection;
        this.maxSaturation = maxSaturation;
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

    public int getMaxHappiness() {
        return maxHappiness;
    }

    public int getStartingAffection() {
        return startingAffection;
    }

    public int getMaxSaturation() {
        return maxSaturation;
    }
}
