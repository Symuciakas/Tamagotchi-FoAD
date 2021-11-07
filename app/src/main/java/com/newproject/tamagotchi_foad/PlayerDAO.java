package com.newproject.tamagotchi_foad;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PlayerDAO {
    @Insert(onConflict = REPLACE)
    void insert(Player player);

    @Delete
    void delete(Player player);

    @Delete
    void reset(List<Player> playerList);

    @Query("UPDATE players SET Experience = :sExperience, `Fed food count` = :sFoodFed, Name = :sName, Level = :sLevel, `Given pat count` = :sPatsGiven, Score = :sScore, `Play time` = :sPlayTime WHERE uid == :sUID")
    void update(long sUID, String sName, int sLevel, int sExperience, int sScore, int sPlayTime, int sPatsGiven, int sFoodFed);

    @Query("UPDATE players SET `Play time` = :sPlayTime WHERE uid == :sUID")
    void updatePlayTime(long sUID, int sPlayTime);

    @Query("SELECT * FROM players WHERE uid == :sUID")
    Player get(int sUID);

    @Query("SELECT * FROM players")
    List<Player> getAll();
}
