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

        homeFragmentView = new HomeFragmentView(HomeFragment.this, null);
        homeFragmentView.initializeViews();
        homeFragmentController = new HomeFragmentController(HomeFragment.this, homeFragmentView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void signOut(View view){
        WorkManager.getInstance(this.getActivity().getApplicationContext()).cancelAllWork();
        FirebaseAuth.getInstance().signOut();
        this.getActivity().finish();
    }


    public void updateFirebase(View view){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest updateFirebaseWorkRequest = new OneTimeWorkRequest.Builder(UpdateFireBaseWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this.getActivity().getApplicationContext()).enqueue(updateFirebaseWorkRequest);

    }

    public void seeLocalDatabase(View view){
        LocalDatabaseHelper localDatabaseHelper = new LocalDatabaseHelper(this.getActivity().getApplicationContext());

        Cursor cursor = localDatabaseHelper.getData();

        if(cursor.getCount() <= 0){
            Log.i("CURSOR", "NULL");
            return;
        }

        while(cursor.moveToNext()){

            Log.i("Database", cursor.getString(0) + "\t:\t" + cursor.getInt(1));
        }

        cursor.close();
    }
}