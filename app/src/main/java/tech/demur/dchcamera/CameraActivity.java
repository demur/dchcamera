package tech.demur.dchcamera;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.VideoResult;

import java.io.File;
import java.util.Locale;

import tech.demur.dchcamera.databinding.ActivityCameraBinding;

import static tech.demur.dchcamera.MainActivity.EXTRA_DURATION_KEY;
import static tech.demur.dchcamera.MainActivity.EXTRA_FILENAME_KEY;
import static tech.demur.dchcamera.MainActivity.EXTRA_TIMESTAMP_KEY;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private ActivityCameraBinding mCameraBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getExtras()
                && getIntent().hasExtra(EXTRA_FILENAME_KEY)
                && getIntent().hasExtra(EXTRA_DURATION_KEY)) {
            String filename = getIntent().getStringExtra(EXTRA_FILENAME_KEY);
            final File file = new File(
                    this.getFilesDir(),
                    filename.toLowerCase().endsWith(getString(R.string.video_extension))
                            ? filename
                            : filename + getString(R.string.video_extension));
            int duration = getIntent().getIntExtra(EXTRA_DURATION_KEY, 0);
            if (null != getSupportActionBar()) {
                getSupportActionBar().hide();
            }
            mCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
            mCameraBinding.camera.setLifecycleOwner(this);

            mCameraBinding.camera.setVideoMaxDuration(duration * 1_000);
            mCameraBinding.camera.addCameraListener(new CameraListener() {
                @Override
                public void onVideoTaken(@NonNull VideoResult result) {
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(EXTRA_FILENAME_KEY, filename);
                    replyIntent.putExtra(EXTRA_DURATION_KEY, duration);
                    replyIntent.putExtra(EXTRA_TIMESTAMP_KEY, System.currentTimeMillis());
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }

                @Override
                public void onCameraError(@NonNull CameraException exception) {
                    super.onCameraError(exception);
                    Intent replyIntent = new Intent();
                    setResult(RESULT_CANCELED, replyIntent);
                    finish();
                }

                @Override
                public void onVideoRecordingStart() {
                    super.onVideoRecordingStart();
                    new CountDownTimer(duration * 1_000, 1_000) {
                        @Override
                        public void onTick(long l) {
                            int sec = Math.round(l / 1_000f);
                            mCameraBinding.timer.setText(String.format(Locale.US, "%d:%02d", sec / 60, sec % 60));
                        }

                        @Override
                        public void onFinish() {
                            mCameraBinding.timer.setText(R.string.video_capturing_finished);
                        }
                    }.start();
                }
            });

            mCameraBinding.camera.takeVideo(file);
            mCameraBinding.timer.setText(R.string.video_capturing_steady);

            new CountDownTimer(6_000, 1_000) {
                @Override
                public void onTick(long tick) {
                    if (file.exists()) {
                        mCameraBinding.timer.setText(R.string.video_capturing_starting);
                        this.cancel();
                        return;
                    }
                    mCameraBinding.camera.takeVideo(file);
                }

                @Override
                public void onFinish() {
                    Intent replyIntent = new Intent();
                    setResult(RESULT_CANCELED, replyIntent);
                    finish();
                }
            }.start();
        } else {
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    R.string.error_no_data_for_camera_activity,
                    Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            if (null != v) {
                v.setGravity(Gravity.CENTER);
            }
            toast.show();
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}