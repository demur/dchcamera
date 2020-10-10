package tech.demur.dchcamera.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecordingRepository {
    private final RecordingDao mRecordingDao;

    public RecordingRepository(Application application) {
        RecordingRoomDB db = RecordingRoomDB.get(application);
        mRecordingDao = db.recordingDao();
    }

    public LiveData<List<Recording>> getAllLive() {
        return mRecordingDao.allLive();
    }

    public LiveData<List<String>> getNamesLive() {
        return mRecordingDao.getLiveNames();
    }

    public void insert(Recording recording) {
        RecordingRoomDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRecordingDao.insert(recording);
            }
        });
    }
}