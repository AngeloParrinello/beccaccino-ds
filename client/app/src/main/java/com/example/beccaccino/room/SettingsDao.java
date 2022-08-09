package com.example.beccaccino.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingsDao {
    @Update
    void updateSettings(Settings settings);

    @Query("SELECT * FROM settings LIMIT 1")
    LiveData<Settings> getSettings();

    @Insert
    void insertSettings(Settings settings);
}
