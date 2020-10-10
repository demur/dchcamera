package tech.demur.dchcamera;

import android.app.Application;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import tech.demur.dchcamera.adapters.RecordingAdapter;
import tech.demur.dchcamera.database.Recording;
import tech.demur.dchcamera.database.RecordingRepository;

public class MainViewModel extends AndroidViewModel {
    private final RecordingRepository mRepository;
    private final LiveData<List<Recording>> allRecordingsLive;
    private final LiveData<List<String>> allNamesLive;
    private RecordingAdapter adapter;
    public ObservableInt loading;
    public ObservableInt showEmpty;
    public ObservableFloat slider = new ObservableFloat(20f);
    public ObservableBoolean fabEnabled = new ObservableBoolean(false);
    private List<String> existingNames = new ArrayList<>();
    public ObservableInt errorFilenameMessage = new ObservableInt(R.string.empty);
    public ObservableField<String> filename = new ObservableField<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RecordingRepository(application);
        allRecordingsLive = mRepository.getAllLive();
        allNamesLive = mRepository.getNamesLive();
        adapter = new RecordingAdapter(R.layout.recording_rv_item, this);
        loading = new ObservableInt(View.GONE);
        showEmpty = new ObservableInt(View.GONE);
    }

    public LiveData<List<Recording>> getRecordingsLive() {
        return allRecordingsLive;
    }

    public LiveData<List<String>> getAllNamesLive() {
        return allNamesLive;
    }

    public List<String> getAllNames() {
        List<String> list = allNamesLive.getValue();
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, list.get(i).toLowerCase());
            }
            return list;
        }
        return new ArrayList<>();
    }

    public void insertRecording(Recording recording) {
        mRepository.insert(recording);
    }

    public RecordingAdapter getAdapter() {
        return adapter;
    }

    public Recording getAdapterRecordingAt(Integer index) {
        return adapter.getRecordingAt(index);
    }

    public void setRecordingsInAdapter(List<Recording> recordings) {
        this.adapter.setRecordings(recordings);
        this.adapter.notifyDataSetChanged();
    }

    public ObservableFloat getSlider() {
        return slider;
    }

    public void setSlider(ObservableFloat slider) {
        this.slider = slider;
    }

    public ObservableBoolean getFabEnabled() {
        return fabEnabled;
    }

    public ObservableInt getErrorFilenameMessage() {
        return errorFilenameMessage;
    }

    public void setExistingNames(List<String> existingNames) {
        this.existingNames = existingNames;
    }

    public void afterTextChangedName(Editable s) {
        if (s.toString().replaceAll("^\\s+", "").isEmpty()) {
            fabEnabled.set(false);
            return;
        }
        if (existingNames.contains(s.toString().replaceAll("^\\s+", "").toLowerCase())) {
            errorFilenameMessage.set(R.string.error_name_exists);
            fabEnabled.set(false);
        } else {
            errorFilenameMessage.set(R.string.empty);
            fabEnabled.set(true);
        }
    }
}