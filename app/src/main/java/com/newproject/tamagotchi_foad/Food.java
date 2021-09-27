package com.newproject.tamagotchi_foad;

public class Food {
    private int id;
    private String name;
    private int value;
    private int cost;

    /**
     * Constructors
     */

    public Food() {

    }

    public Food(int id, String name, int value, int cost) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.cost = cost;
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

    public int getValue() {
        return value;
    }

    public int getCost() {
        return cost;
    }
}
