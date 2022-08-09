package com.example.beccaccino.repository;

import androidx.lifecycle.LiveData;
import com.example.beccaccino.model.entities.PlayImpl;
import com.example.beccaccino.room.Metadata;

import java.util.List;

public interface BeccaccinoRepository {
    void addPlay(PlayImpl play);

    LiveData<List<PlayImpl>> getPlays();

    void clearPlays();

    LiveData<Metadata> getMetadata();

    void addMetadata(Metadata metadata);

    void updateMetadata(Metadata metadata);

    void clearMetadata();
}
