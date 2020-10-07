package tech.demur.dchcamera.bindings;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomViewBindings {
    @BindingAdapter("setAdapter")
    public static void bindRecyclerViewAdapter(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter("error")
    public static void setError(TextInputLayout textInputLayout, Object strOrResId) {
        if (null == strOrResId) {
            textInputLayout.setError(null);
        } else if (strOrResId instanceof Integer) {
            String retrieved = textInputLayout.getContext().getString((Integer) strOrResId);
            if (TextUtils.isEmpty(retrieved)) {
                textInputLayout.setError(null);
            } else {
                textInputLayout.setError(retrieved);
            }
        } else {
            textInputLayout.setError((String) strOrResId);
        }
    }

    @BindingAdapter("date")
    public static void convToDate(TextView textView, long milliSeconds) {
        textView.setText(new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US).format(new Date(milliSeconds)));
    }

    @BindingAdapter("duration")
    public static void formatDuration(TextView textView, int seconds) {
        textView.setText(String.format(Locale.US, "%d:%02d", (int) seconds / 60, (int) seconds % 60));
    }

    @BindingAdapter("duration")
    public static void formatDuration(TextView textView, float seconds) {
        formatDuration(textView, Math.round(seconds));
    }

    @InverseBindingAdapter(attribute = "android:value")
    public static float getSliderValue(Slider slider) {
        return slider.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setSliderListeners(Slider slider, final InverseBindingListener attrChange) {
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                attrChange.onChange();
            }
        });
    }
}