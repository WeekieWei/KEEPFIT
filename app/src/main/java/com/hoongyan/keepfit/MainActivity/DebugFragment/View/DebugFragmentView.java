package com.hoongyan.keepfit.MainActivity.DebugFragment.View;

import android.os.Handler;
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

    @Override
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_debug, container, false);
        initializeViews();
        return rootView;
    }

    @Override
    public void startDebugging(){
        handler.postDelayed(runnable, 0);
    }

    @Override
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

    @Override
    public void refreshUserID(){
        userId.setText(": " + debugFragmentController.getUserID());
    }

    @Override
    public void refreshSensorType(){
        sensorType.setText(": " + debugFragmentController.getSensorType());
    }

    @Override
    public void refreshCurrentSteps(){
        currentSteps.setText(": " + debugFragmentController.getCurrentSteps());
    }

    @Override
    public void refreshPedometerState(){
        pedometerState.setText(": " + debugFragmentController.getWorkerState("StepTrackingWorker"));
    }

    @Override
    public void refreshDailyRefreshState(){
        dailyRefreshState.setText(": " + debugFragmentController.getWorkerState("DailyRefreshWorker"));
    }

    @Override
    public void refreshFirebaseSyncState(){
        firebaseSyncState.setText(": " + debugFragmentController.getWorkerState("UpdateFirebaseWorker"));
    }

    @Override
    public void refreshLocalDatabaseContent(){
        localDatabaseContent.setText(debugFragmentController.getLocalDatabaseContent());
    }

    @Override
    public void refreshDatabaseLastUpdate(){
        localDatabaseLastUpdate.setText(": " + debugFragmentController.getLocalDatabaseLastUpdate());
    }

    @Override
    public void refreshLastDailyReset(){
        lastDailyResetTrigger.setText(": " + debugFragmentController.getLastDailyResetTrigger());
    }

    @Override
    public void refreshLastFirebaseTrigger(){
        lastFirebaseTrigger.setText(": " + debugFragmentController.getLastFirebaseTrigger());
    }

    @Override
    public void refreshLastFirebaseUpdate(){
        lastFirebaseUpdate.setText(": " + debugFragmentController.getLastFirebaseUpdate());
    }

    @Override
    public void refreshLastFirebaseValue(){
        lastFirebaseValue.setText(": " + debugFragmentController.getLastFirebaseUpdateValue());
    }
}
