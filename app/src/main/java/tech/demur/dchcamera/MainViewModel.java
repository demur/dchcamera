package tech.demur.dchcamera;

import android.app.Application;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.material.slider.LabelFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public float sliderStep, sliderMax, sliderMin;
    private List<String> existingNames = new ArrayList<>();
    public ObservableInt errorFilenameMessage = new ObservableInt(R.string.empty);
    public ObservableField<String> filename = new ObservableField<>();
    public ObservableBoolean fabEnabled = new ObservableBoolean(filename, errorFilenameMessage) {
        @Override
        public boolean get() {
            return !TextUtils.isEmpty(filename.get()) && errorFilenameMessage.get() == R.string.empty;
        }
    };
    private LabelFormatter sliderLabelFormatter = new LabelFormatter() {
        @NonNull
        @Override
        public String getFormattedValue(float value) {
            return String.format(Locale.US, "%d:%02d", (int) value / 60, (int) value % 60);
        }
    };

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
        if (TextUtils.isEmpty(s.toString().replaceAll("^\\s+", ""))) {
            return;
        }
        if (existingNames.contains(s.toString().replaceAll("^\\s+", "").toLowerCase())) {
            errorFilenameMessage.set(R.string.error_name_exists);
        } else {
            errorFilenameMessage.set(R.string.empty);
        }
    }

    public void sliderInc() {
        if (slider.get() + sliderStep <= sliderMax) {
            slider.set(slider.get() + sliderStep);
        }
    }

    public void sliderDec() {
        if (slider.get() - sliderStep >= sliderMin) {
            slider.set(slider.get() - sliderStep);
        }
    }

    public LabelFormatter getSliderLabelFormatter() {
        return sliderLabelFormatter;
    }

    private InputFilter filenameFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            boolean isChanged = false;
            boolean isLeadingWhitespace = dstart == 0;
            StringBuilder sb = new StringBuilder(end - start);
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (isValidFatFilenameChar(c) && !(isLeadingWhitespace && Character.isWhitespace(c))) {
                    sb.append(c);
                } else {
                    isChanged = true;
                }
                if (isLeadingWhitespace && !Character.isWhitespace(c)) {
                    isLeadingWhitespace = false;
                }
            }
            if (isChanged) {
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(sb);
                    TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                    return sp;
                }
                return sb;
            }
            return null;
        }

        private boolean isValidFatFilenameChar(char c) {
            if ((0x00 <= c && c <= 0x1f)) {
                return false;
            }
            switch (c) {
                case '"':
                case '*':
                case '/':
                case ':':
                case '<':
                case '>':
                case '?':
                case '\\':
                case '|':
                case 0x7F:
                    return false;
                default:
                    return true;
            }
        }
    };

    public InputFilter getFilenameFilter() {
        return filenameFilter;
    }
}