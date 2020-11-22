package com.hoongyan.keepfit.MainActivity.UpdateProfile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MainActivity.HomeFragment.Controller.HomeFragmentController;
import com.hoongyan.keepfit.MainActivity.HomeFragment.HomeFragment;
import com.hoongyan.keepfit.MainActivity.HomeFragment.View.HomeFragmentView;
import com.hoongyan.keepfit.MainActivity.UpdateProfile.Controller.UpdateProfileController;
import com.hoongyan.keepfit.MainActivity.UpdateProfile.View.UpdateProfileView;
import com.hoongyan.keepfit.R;

public class UpdateProfileFragment extends Fragment {

    //MVC Components
    private UpdateProfileView updateProfileView;
    private UpdateProfileController updateProfileController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateProfileView = new UpdateProfileView(UpdateProfileFragment.this);
        updateProfileController = new UpdateProfileController(getActivity().getApplicationContext(), updateProfileView);
        updateProfileView.setMVCController(updateProfileController);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return updateProfileView.inflateFragmentLayout(inflater, container);
    }
}