package tech.demur.dchcamera.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Recording.class}, version = 1, exportSchema = false)
public abstract class RecordingRoomDB extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile RecordingRoomDB sInstance = null;

    public synchronized static RecordingRoomDB get(final Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                    RecordingRoomDB.class, "recording_database")
                    .build();
        }
        return sInstance;
    }

    public abstract RecordingDao recordingDao();
}