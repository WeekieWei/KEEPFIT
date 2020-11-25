package com.hoongyan.keepfit.MainActivity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.R;

public class MainActivityController implements MainActivityControllerInterface {

    //MVC Components
    private MainActivityView mainActivityView;

    //Data Field
    private Activity mainActivity;
    private NavController navController;

    public MainActivityController(final Activity mainActivity, final MainActivityView mainActivityView){
        this.mainActivity = mainActivity;
        this.mainActivityView = mainActivityView;
        navController = Navigation.findNavController(mainActivity, R.id.navHostFragment);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                mainActivityView.getTextTitle().setText(destination.getLabel());
            }
        });

        createNotificationChannel();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);

            final String NOTIFICATION_PEDOMETER_CHANNEL_ID = mainActivity.getString(R.string.notification_pedometer_channel_id);
            final String NOTIFICATION_PEDOMETER_CHANNEL_NAME = mainActivity.getString(R.string.notification_pedometer_channel_name);
            final String NOTIFICATION_PEDOMETER_CHANNEL_DESCRIPTION = mainActivity.getString(R.string.notification_pedometer_channel_description);

            NotificationChannel pedometerChannel = new NotificationChannel(NOTIFICATION_PEDOMETER_CHANNEL_ID, NOTIFICATION_PEDOMETER_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            pedometerChannel.setDescription(NOTIFICATION_PEDOMETER_CHANNEL_DESCRIPTION);


            final String NOTIFICATION_FIREBASE_CHANNEL_ID = mainActivity.getString(R.string.notification_firebase_channel_id);
            final String NOTIFICATION_FIREBASE_CHANNEL_NAME = mainActivity.getString(R.string.notification_firebase_channel_name);
            final String NOTIFICATION_FIREBASE_CHANNEL_DESCRIPTION = mainActivity.getString(R.string.notification_firebase_channel_description);

            NotificationChannel firebaseChannel = new NotificationChannel(NOTIFICATION_FIREBASE_CHANNEL_ID, NOTIFICATION_FIREBASE_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            firebaseChannel.setDescription(NOTIFICATION_FIREBASE_CHANNEL_DESCRIPTION);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(pedometerChannel);
                notificationManager.createNotificationChannel(firebaseChannel);
            }
        }
    }

    @Override
    public void openNavigationDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public NavController getNavController(){
        return navController;
    }

}
