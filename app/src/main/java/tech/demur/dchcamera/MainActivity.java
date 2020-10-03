package tech.demur.dchcamera;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import tech.demur.dchcamera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String RECORDINGS_TABLE = "recordings";
    ActivityMainBinding mMainBinging;
    MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinging = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
}