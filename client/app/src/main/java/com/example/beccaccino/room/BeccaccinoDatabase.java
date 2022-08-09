package com.example.beccaccino.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.beccaccino.model.entities.PlayImpl;


@Database(entities = {Metadata.class, PlayImpl.class, Settings.class}, version = 5, exportSchema = false)
@TypeConverters({BeccaccinoTypeConverter.class})
public abstract class BeccaccinoDatabase extends RoomDatabase {
    private static volatile BeccaccinoDatabase INSTANCE;

    /*
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    */
    public static synchronized BeccaccinoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeccaccinoDatabase.class, "beccaccino_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract GameDao gameDao();

    public abstract SettingsDao settingsDao();

/*
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };


    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private SettingsDao settingsDao;

        private PopulateDbAsyncTask(BeccaccinoDatabase db){
            settingsDao = db.settingsDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            settingsDao.insertSettings(new Settings());
            return null;
        }
    }

*/
}
