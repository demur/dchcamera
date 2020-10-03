package tech.demur.dchcamera;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import tech.demur.dchcamera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String RECORDINGS_TABLE = "recordings";
    public static final String EXTRA_FILENAME_KEY = "filename";
    public static final String EXTRA_DURATION_KEY = "duration";
    public static final String EXTRA_TIMESTAMP_KEY = "timestamp";
    ActivityMainBinding mMainBinding;
    MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
}