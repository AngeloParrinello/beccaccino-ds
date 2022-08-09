package com.example.beccaccino.repository;

import androidx.lifecycle.LiveData;

import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.PlayImpl;
import com.example.beccaccino.model.logic.TurnOrder;
import com.example.beccaccino.room.Metadata;
import com.example.beccaccino.room.Settings;

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
