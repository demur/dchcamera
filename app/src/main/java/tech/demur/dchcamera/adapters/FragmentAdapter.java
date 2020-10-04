package tech.demur.dchcamera.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import tech.demur.dchcamera.fragments.ListFragment;
import tech.demur.dchcamera.fragments.PresetFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PresetFragment();
            case 1:
                return new ListFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}