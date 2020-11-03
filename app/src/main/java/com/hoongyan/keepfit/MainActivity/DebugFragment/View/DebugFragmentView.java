package com.hoongyan.keepfit.MainActivity.DebugFragment.View;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hoongyan.keepfit.MainActivity.DebugFragment.Controller.DebugFragmentController;
import com.hoongyan.keepfit.R;

public class DebugFragmentView implements DebugFragmentViewInterface {

    //MVC Components
    DebugFragmentController debugFragmentController;

    //Data Field
    private View rootView;
    private Handler handler;
    private Runnable runnable;


    //Views
    private TextView userId, sensorType, currentSteps, pedometerState, dailyRefreshState, firebaseSyncState,
            localDatabaseContent, localDatabaseLastUpdate, lastDailyResetTrigger,
            lastFirebaseTrigger, lastFirebaseUpdate, lastFirebaseValue;

    private Button updateLocalDatabaseButton, updateFirebaseButton;



    public DebugFragmentView(DebugFragmentController debugFragmentController){
        this.debugFragmentController = debugFragmentController;

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                refreshAllFields();
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    public void initializeViews() {
        userId = rootView.findViewById(R.id.debug_userID);
        sensorType = rootView.findViewById(R.id.debug_sensorType);
        currentSteps = rootView.findViewById(R.id.debug_currentSteps);
        pedometerState = rootView.findViewById(R.id.debug_pedometerState);
        dailyRefreshState = rootView.findViewById(R.id.debug_dailyRefreshState);
        firebaseSyncState = rootView.findViewById(R.id.debug_firebaseSyncState);
        localDatabaseContent = rootView.findViewById(R.id.debug_localDatabaseContent);
        localDatabaseLastUpdate = rootView.findViewById(R.id.debug_localDatabaseLastUpdate);
        lastDailyResetTrigger = rootView.findViewById(R.id.debug_lastDailyResetTrigger);
        lastFirebaseTrigger = rootView.findViewById(R.id.debug_lastFirebaseTrigger);
        lastFirebaseUpdate = rootView.findViewById(R.id.debug_lastFirebaseUpdate);
        lastFirebaseValue = rootView.findViewById(R.id.debug_lastFirebaseValue);

        updateLocalDatabaseButton = rootView.findViewById(R.id.debug_updateLocalDatabaseButton);
        updateLocalDatabaseButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                debugFragmentController.updateLocalDatabase();
                Toast.makeText(rootView.getContext(), "Triggered", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        updateFirebaseButton = rootView.findViewById(R.id.debug_updateFirebaseButton);
        updateFirebaseButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                debugFragmentController.updateFirebase();
                Toast.makeText(rootView.getContext(), "Triggered", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_debug, container, false);
        initializeViews();
        return rootView;
    }

    public void startDebugging(){
        handler.postDelayed(runnable, 0);
    }

    public void stopDebugging(){
        handler.removeCallbacks(runnable);
    }

    private void refreshAllFields(){
        refreshUserID();
        refreshSensorType();
        refreshCurrentSteps();
        refreshPedometerState();
        refreshDailyRefreshState();
        refreshFirebaseSyncState();
        refreshLocalDatabaseContent();
        refreshDatabaseLastUpdate();
        refreshLastDailyReset();
        refreshLastFirebaseTrigger();
        refreshLastFirebaseUpdate();
        refreshLastFirebaseValue();
    }

    public void refreshUserID(){
        userId.setText(": " + debugFragmentController.getUserID());
    }

    public void refreshSensorType(){
        sensorType.setText(": " + debugFragmentController.getSensorType());
    }

    public void refreshCurrentSteps(){
        currentSteps.setText(": " + debugFragmentController.getCurrentSteps());
    }

    public void refreshPedometerState(){
        pedometerState.setText(": " + debugFragmentController.getWorkerState("StepTrackingWorker"));
    }

    public void refreshDailyRefreshState(){
        dailyRefreshState.setText(": " + debugFragmentController.getWorkerState("DailyRefreshWorker"));
    }

    public void refreshFirebaseSyncState(){
        firebaseSyncState.setText(": " + debugFragmentController.getWorkerState("UpdateFirebaseWorker"));
    }

    public void refreshLocalDatabaseContent(){
        localDatabaseContent.setText(debugFragmentController.getLocalDatabaseContent());
    }

    public void refreshDatabaseLastUpdate(){
        localDatabaseLastUpdate.setText(": " + debugFragmentController.getLocalDatabaseLastUpdate());
    }

    public void refreshLastDailyReset(){
        lastDailyResetTrigger.setText(": " + debugFragmentController.getLastDailyResetTrigger());
    }

    public void refreshLastFirebaseTrigger(){
        lastFirebaseTrigger.setText(": " + debugFragmentController.getLastFirebaseTrigger());
    }

    public void refreshLastFirebaseUpdate(){
        lastFirebaseUpdate.setText(": " + debugFragmentController.getLastFirebaseUpdate());
    }

    public void refreshLastFirebaseValue(){
        lastFirebaseValue.setText(": " + debugFragmentController.getLastFirebaseUpdateValue());
    }
}
