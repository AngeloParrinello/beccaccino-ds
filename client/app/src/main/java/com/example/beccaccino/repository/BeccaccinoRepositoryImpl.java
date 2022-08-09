package com.example.beccaccino.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.entities.PlayImpl;
import com.example.beccaccino.model.logic.TurnOrder;
import com.example.beccaccino.room.BeccaccinoDatabase;
import com.example.beccaccino.room.GameDao;
import com.example.beccaccino.room.Metadata;
import com.example.beccaccino.room.Settings;
import com.example.beccaccino.room.SettingsDao;

import java.util.List;

public class BeccaccinoRepositoryImpl implements BeccaccinoRepository{
    private GameDao gameDao;
    private LiveData<List<PlayImpl>> plays;
    private LiveData<Metadata> metadata;

    public BeccaccinoRepositoryImpl(Application application){
        BeccaccinoDatabase database = BeccaccinoDatabase.getDatabase(application);
        this.gameDao = database.gameDao();
        this.plays = gameDao.getPlays();
        this.metadata = gameDao.getMetadata();
    }


    @Override
    public void addPlay(PlayImpl play) {
        new AddPlayAsyncTask(gameDao).execute(play);
    }

    @Override
    public LiveData<List<PlayImpl>> getPlays() {
        return this.plays;
    }

    @Override
    public void clearPlays() {
        new ClearPlaysAsyncTask(gameDao).execute();
    }

    @Override
    public LiveData<Metadata> getMetadata() {
        return this.metadata;
    }

    @Override
    public void addMetadata(Metadata metadata) {
        new AddMetadataAsyncTask(gameDao).execute(metadata);
    }

    @Override
    public void updateMetadata(Metadata metadata) {
        new UpdateMetadataAsyncTask(gameDao).execute(metadata);
    }

    @Override
    public void clearMetadata() {
        new ClearMetadataAsyncTask(gameDao).execute();
    }

    private static class AddPlayAsyncTask extends AsyncTask<PlayImpl, Void, Void>{
        private GameDao gameDao;
        private AddPlayAsyncTask(GameDao gameDao){
            this.gameDao = gameDao;
        }
        @Override
        protected Void doInBackground(PlayImpl... plays) {
            gameDao.addPlay(plays[0]);
            return null;
        }
    }


    private static class ClearPlaysAsyncTask extends AsyncTask<Void, Void, Void>{
        private GameDao gameDao;
        private ClearPlaysAsyncTask(GameDao gameDao){
            this.gameDao = gameDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            gameDao.clearPlays();
            return null;
        }
    }

    private static class ClearMetadataAsyncTask extends AsyncTask<Void, Void, Void>{
        private GameDao gameDao;
        private ClearMetadataAsyncTask(GameDao gameDao){
            this.gameDao = gameDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            gameDao.clearMetadata();
            return null;
        }
    }


    private static class AddMetadataAsyncTask extends AsyncTask<Metadata, Void, Void>{
        private GameDao gameDao;
        private AddMetadataAsyncTask(GameDao gameDao){
            this.gameDao = gameDao;
        }
        @Override
        protected Void doInBackground(Metadata... metadatas) {
            gameDao.addMetadata(metadatas[0]);
            return null;
        }
    }


    private static class UpdateMetadataAsyncTask extends AsyncTask<Metadata, Void, Void>{
        private GameDao gameDao;
        private UpdateMetadataAsyncTask(GameDao gameDao){
            this.gameDao = gameDao;
        }
        @Override
        protected Void doInBackground(Metadata... metadatas) {
            gameDao.updateMetadata(metadatas[0]);
            return null;
        }
    }
    /*
    private static class UpdateSettingsAsyncTask extends AsyncTask<Settings, Void, Void>{
        private SettingsDao settingsDao;
        private UpdateSettingsAsyncTask(SettingsDao settingsDao){
            this.settingsDao = settingsDao;
        }
        @Override
        protected Void doInBackground(Settings... settings) {
            settingsDao.updateSettings(settings[0]);
            return null;
        }
    }
     */
}
