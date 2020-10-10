package tech.demur.dchcamera.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.LabelFormatter;
import com.otaliastudios.cameraview.CameraUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.demur.dchcamera.CameraActivity;
import tech.demur.dchcamera.MainViewModel;
import tech.demur.dchcamera.R;
import tech.demur.dchcamera.database.Recording;
import tech.demur.dchcamera.databinding.FragmentPresetBinding;

import static tech.demur.dchcamera.MainActivity.EXTRA_DURATION_KEY;
import static tech.demur.dchcamera.MainActivity.EXTRA_FILENAME_KEY;
import static tech.demur.dchcamera.MainActivity.EXTRA_TIMESTAMP_KEY;

public class PresetFragment extends Fragment {
    private static final String TAG = PresetFragment.class.getSimpleName();
    private static final int REQUEST_CODE_PERMISSIONS = 8788;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"};
    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 6215;
    private MainViewModel mViewModel;
    FragmentPresetBinding mBinding;

    public PresetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != getActivity()) {
            mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_preset, container, false);
        mBinding.setModel(mViewModel);
        mBinding.slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%d:%02d", (int) value / 60, (int) value % 60);
            }
        });
        mViewModel.sliderStep = mBinding.slider.getStepSize();
        mViewModel.sliderMax = mBinding.slider.getValueTo();
        mViewModel.sliderMin = mBinding.slider.getValueFrom();
        mViewModel.getAllNamesLive().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (null != strings) {
                    for (int i = 0; i < strings.size(); i++) {
                        strings.set(i, strings.get(i).toLowerCase());
                    }
                    mViewModel.setExistingNames(strings);
                } else {
                    mViewModel.setExistingNames(new ArrayList<>());
                }
            }
        });
        mBinding.fab.setOnClickListener(fabClickListener);
        mBinding.filename.getEditText().setFilters(new InputFilter[]{filenameFilter});
        return mBinding.getRoot();
    }

    public boolean allPermissionsGranted() {
        if (null != getContext() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(getContext(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(
                        getContext(),
                        R.string.error_permissions_not_granted,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCamera() {
        if (null != getContext() && CameraUtils.hasCameras(getContext())) {
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            intent.putExtra(EXTRA_FILENAME_KEY,
                    mViewModel.filename.get().replaceAll("^\\s+", ""));
            intent.putExtra(EXTRA_DURATION_KEY, (int) mViewModel.slider.get());
            startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.filename.set("");
            String name = data.getStringExtra(EXTRA_FILENAME_KEY);
            int duration = data.getIntExtra(EXTRA_DURATION_KEY, -1);
            long timestamp = data.getLongExtra(EXTRA_TIMESTAMP_KEY, -1L);
            if (null != name && name.length() > 0 && duration > 0 && timestamp > 0) {
                mViewModel.insertRecording(new Recording(name, duration, timestamp));
            }
        } else {
            Toast.makeText(
                    getContext(),
                    R.string.invalid_camera_activity_result,
                    Toast.LENGTH_LONG).show();
        }
    }

    final View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!allPermissionsGranted()) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            } else {
                startCamera();
            }
        }
    };

    InputFilter filenameFilter = new InputFilter() {
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
    };

    private static boolean isValidFatFilenameChar(char c) {
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
}