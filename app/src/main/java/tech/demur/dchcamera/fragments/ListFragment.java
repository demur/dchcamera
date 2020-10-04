package tech.demur.dchcamera.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import tech.demur.dchcamera.MainViewModel;
import tech.demur.dchcamera.R;
import tech.demur.dchcamera.database.Recording;
import tech.demur.dchcamera.databinding.FragmentListBinding;

public class ListFragment extends Fragment {
    private MainViewModel mViewModel;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != getActivity()) {
            mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        }
        FragmentListBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_list, container, false);
        binding.setModel(mViewModel);
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        mViewModel.loading.set(View.VISIBLE);
        mViewModel.getRecordingsLive().observe(getViewLifecycleOwner(), new Observer<List<Recording>>() {
            @Override
            public void onChanged(List<Recording> recordings) {
                mViewModel.loading.set(View.GONE);
                if (recordings.size() == 0) {
                    mViewModel.showEmpty.set(View.VISIBLE);
                } else {
                    mViewModel.showEmpty.set(View.GONE);
                    mViewModel.setRecordingsInAdapter(recordings);
                }
            }
        });
    }
}