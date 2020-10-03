package tech.demur.dchcamera;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import tech.demur.dchcamera.database.Recording;
import tech.demur.dchcamera.database.RecordingRoomDB;

public class MainViewModel extends AndroidViewModel {
    private final RecordingRoomDB mDb;
    private final LiveData<List<Recording>> allRecordingsLive;
    private final LiveData<List<String>> allNamesLive;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mDb = RecordingRoomDB.get(this.getApplication());
        allRecordingsLive = mDb.recordingDao().allLive();
        allNamesLive = mDb.recordingDao().getLiveNames();
    }
}