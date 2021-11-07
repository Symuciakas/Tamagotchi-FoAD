package com.newproject.tamagotchi_foad;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PetDAO {
    @Insert(onConflict = REPLACE)
    void insert(Pet pet);

    @Delete
    void delete(Pet pet);

    @Delete
    void reset(List<Pet> pet);

    @Query("UPDATE PlayerPets SET Experience = :sExperience, Level = :sLevel, Health = :sHealth, Happiness = :sHappiness, Affection = :sAffection, Saturation = :sSaturation WHERE uid == :sUID")
    void update(long sUID, int sLevel, int sExperience, int sAffection, int sHealth, int sHappiness, int sSaturation);

    @Query("SELECT * FROM PlayerPets WHERE uid == :sUID")
    Pet get(int sUID);

    @Query("SELECT * FROM PlayerPets")
    List<Pet> getAll();
}
