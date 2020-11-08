package com.hoongyan.keepfit.MainActivity.HomeFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hoongyan.keepfit.JavaClass.LocalDatabaseHelper;
import com.hoongyan.keepfit.MainActivity.HomeFragment.Controller.HomeFragmentController;
import com.hoongyan.keepfit.MainActivity.HomeFragment.View.HomeFragmentView;
import com.hoongyan.keepfit.MainActivity.MainActivity;
import com.hoongyan.keepfit.MainActivity.MainActivityController;
import com.hoongyan.keepfit.MainActivity.MainActivityView;
import com.hoongyan.keepfit.R;
import com.hoongyan.keepfit.Workers.UpdateFireBaseWorker;


public class HomeFragment extends Fragment {

    //MVC Components
    private HomeFragmentView homeFragmentView;
    private HomeFragmentController homeFragmentController;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeFragmentView = new HomeFragmentView(HomeFragment.this);
        homeFragmentController = new HomeFragmentController(HomeFragment.this, homeFragmentView);
        homeFragmentView.setMVCController(homeFragmentController);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return homeFragmentView.inflateFragmentLayout(inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        homeFragmentView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        homeFragmentView.onPause();
    }

    public void signOut(View view){
        WorkManager.getInstance(this.getActivity().getApplicationContext()).cancelAllWork();
        FirebaseAuth.getInstance().signOut();
        this.getActivity().finish();
    }

}