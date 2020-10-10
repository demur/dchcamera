package tech.demur.dchcamera;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import tech.demur.dchcamera.adapters.FragmentAdapter;
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

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        mMainBinding.viewpager.setUserInputEnabled(false);
        final ArrayList<String> tabText = new ArrayList<>();
        tabText.add(getString(R.string.record_tab_title));
        tabText.add(getString(R.string.saved_recordings_tab_title));
        mMainBinding.viewpager.setAdapter(fragmentAdapter);
        new TabLayoutMediator(mMainBinding.tabs, mMainBinding.viewpager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabText.get(position));
                    }
                }
        ).attach();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMainBinding.tabs.getSelectedTabPosition() == 0
                && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                mViewModel.sliderInc();
            } else {
                mViewModel.sliderDec();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}