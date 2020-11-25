package com.hoongyan.keepfit.MainActivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.hoongyan.keepfit.R;

public class MainActivityView implements MainActivityViewInterface {

    //MVC Components
    private MainActivityController mainActivityController;


    //Data Field
    private AppCompatActivity mainActivity;
    private View rootView;


    //Views
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView textTitle;

    public MainActivityView(AppCompatActivity mainActivity, ViewGroup viewGroup){
        this.mainActivity = mainActivity;
        rootView = LayoutInflater.from(mainActivity).inflate(R.layout.activity_main, viewGroup);
    }

    @Override
    public void setController(MainActivityController mainActivityController){
        this.mainActivityController = mainActivityController;
    }

    @Override
    public void initializeViews() {
        drawerLayout = rootView.findViewById(R.id.drawerLayout);
        navigationView = rootView.findViewById(R.id.navigationView);
        textTitle = rootView.findViewById(R.id.textTitle);

        rootView.findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityController.openNavigationDrawer(drawerLayout);
            }
        });

        navigationView.setItemIconTintList(null);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public TextView getTextTitle(){
        return textTitle;
    }

    @Override
    public void setupActionBarWithNavController(){
        NavigationUI.setupWithNavController(navigationView, mainActivityController.getNavController());
    }
}
