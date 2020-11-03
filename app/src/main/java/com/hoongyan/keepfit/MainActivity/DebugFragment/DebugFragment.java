package com.hoongyan.keepfit.MainActivity.DebugFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MainActivity.DebugFragment.Controller.DebugFragmentController;
import com.hoongyan.keepfit.MainActivity.DebugFragment.View.DebugFragmentView;
import com.hoongyan.keepfit.R;

public class DebugFragment extends Fragment {

    //MVC Components
    private DebugFragmentView debugFragmentView;
    private DebugFragmentController debugFragmentController;


    public DebugFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debugFragmentController = new DebugFragmentController(getActivity().getApplicationContext());
        debugFragmentView = new DebugFragmentView(debugFragmentController);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return debugFragmentView.inflateFragmentLayout(inflater, container);
    }

    @Override
    public void onPause() {
        super.onPause();
        debugFragmentView.stopDebugging();
    }

    @Override
    public void onResume() {
        super.onResume();
        debugFragmentView.startDebugging();
    }
}