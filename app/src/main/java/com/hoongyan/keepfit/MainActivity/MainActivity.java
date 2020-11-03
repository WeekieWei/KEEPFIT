package com.hoongyan.keepfit.MainActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    //MVC Components
    private MainActivityView mainActivityView;
    private MainActivityController mainActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityView = new MainActivityView(MainActivity.this, null);
        setContentView(mainActivityView.getRootView());
        mainActivityView.initializeViews();

        mainActivityController = new MainActivityController(MainActivity.this, mainActivityView);
        mainActivityView.setController(mainActivityController);
        mainActivityView.setupActionBarWithNavController();
    }

}