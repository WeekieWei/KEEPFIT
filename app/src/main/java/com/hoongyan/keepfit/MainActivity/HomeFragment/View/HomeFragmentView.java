package com.hoongyan.keepfit.MainActivity.HomeFragment.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.hoongyan.keepfit.R;

public class HomeFragmentView implements HomeFragmentViewInterface {

    //MVC Components


    //Data Field
    private View rootView;


    //Views
    TextView stepTextView;

    public HomeFragmentView(Fragment homeFragment, ViewGroup viewGroup){
        rootView = LayoutInflater.from(homeFragment.getContext()).inflate(R.layout.fragment_home, viewGroup);
    }

    @Override
    public void initializeViews() {
        stepTextView = rootView.findViewById(R.id.stepTextView);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public void updateStepTextView(String message){
        stepTextView.setText(message);
    }
}
