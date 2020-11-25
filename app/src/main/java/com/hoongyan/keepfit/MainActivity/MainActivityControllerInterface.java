package com.hoongyan.keepfit.MainActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

public interface MainActivityControllerInterface {

    public void openNavigationDrawer(DrawerLayout drawerLayout);
    public NavController getNavController();
}
