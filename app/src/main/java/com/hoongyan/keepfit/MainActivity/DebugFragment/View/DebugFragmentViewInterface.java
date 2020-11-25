package com.hoongyan.keepfit.MainActivity.DebugFragment.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MVCView;

public interface DebugFragmentViewInterface extends MVCView {

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container);

    public void startDebugging();

    public void stopDebugging();

    public void refreshUserID();

    public void refreshSensorType();

    public void refreshCurrentSteps();

    public void refreshPedometerState();

    public void refreshDailyRefreshState();

    public void refreshFirebaseSyncState();

    public void refreshLocalDatabaseContent();

    public void refreshDatabaseLastUpdate();

    public void refreshLastDailyReset();

    public void refreshLastFirebaseTrigger();

    public void refreshLastFirebaseUpdate();

    public void refreshLastFirebaseValue();
}
