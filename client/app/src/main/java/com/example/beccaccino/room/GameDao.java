package com.example.beccaccino.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.beccaccino.model.entities.PlayImpl;

import java.util.List;

@Dao
public interface GameDao {
    @Query("DELETE FROM plays")
    void clearPlays();

    @Insert
    void addPlay(PlayImpl play);

    @Query("SELECT * from plays ORDER BY uid ASC")
    LiveData<List<PlayImpl>> getPlays();

    @Query("SELECT * from metadata ORDER BY id DESC LIMIT 1")
    LiveData<Metadata> getMetadata();

    @Insert
    void addMetadata(Metadata metadata);

    @Update
    void updateMetadata(Metadata metadata);

    @Query("DELETE FROM metadata")
    void clearMetadata();
}
