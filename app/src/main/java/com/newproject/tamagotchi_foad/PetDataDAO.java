package com.newproject.tamagotchi_foad;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PetDataDAO {
    @Insert(onConflict = REPLACE)
    void insert(PetData petData);

    @Delete
    void delete(PetData petData);

    @Delete
    void reset(List<PetData> petData);

    @Query("UPDATE pets SET `Max health` = :sMaxHealth, `Health loss` = :sHealthLoss, Name = :sName, `Max happiness` = :sMaxHappiness, `Happiness loss` = :sHappinessLoss, `Starting affection` = :sStartingAffection, `Affection loss` = :sAffectionLoss, `Max saturation` = :sMaxSaturation, `Saturation loss` = :sSaturationLoss WHERE uid == :sUID")
    void update(long sUID, String sName, int sMaxHealth, int sHealthLoss, int sMaxHappiness, int sHappinessLoss, int sStartingAffection, int sAffectionLoss, int sMaxSaturation, int sSaturationLoss);

    @Query("SELECT * FROM pets WHERE uid == :sUID")
    PetData get(int sUID);

    @Query("SELECT * FROM pets")
    List<PetData> getAll();
}
