package tech.demur.dchcamera.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tech.demur.dchcamera.MainActivity;

@Dao
public interface RecordingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recording recording);

    @Update
    void update(Recording recording);

    @Delete
    void delete(Recording recording);

    @Query("SELECT * FROM " + MainActivity.RECORDINGS_TABLE + " ORDER BY id DESC")
    List<Recording> all();

    @Query("SELECT * FROM " + MainActivity.RECORDINGS_TABLE + " ORDER BY id ASC")
    List<Recording> allAsc();

    @Query("SELECT * FROM " + MainActivity.RECORDINGS_TABLE + " ORDER BY id DESC")
    LiveData<List<Recording>> allLive();

    @Query("SELECT * FROM " + MainActivity.RECORDINGS_TABLE + " ORDER BY id ASC")
    LiveData<List<Recording>> allAscLive();

    @Query("SELECT name FROM " + MainActivity.RECORDINGS_TABLE)
    List<String> getNames();

    @Query("SELECT name FROM " + MainActivity.RECORDINGS_TABLE)
    LiveData<List<String>> getLiveNames();

    @Query("SELECT * FROM " + MainActivity.RECORDINGS_TABLE + " WHERE id = :id")
    Recording getById(int id);

    @Query("DELETE FROM " + MainActivity.RECORDINGS_TABLE)
    void deleteAll();
}