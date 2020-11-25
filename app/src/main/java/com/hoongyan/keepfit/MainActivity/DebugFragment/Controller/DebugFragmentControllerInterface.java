package com.hoongyan.keepfit.MainActivity.DebugFragment.Controller;

public interface DebugFragmentControllerInterface {
    public String getUserID();

    public String getSensorType();

    public String getCurrentSteps();

    public String getWorkerState(String workUniqueName);

    public String getLocalDatabaseContent();

    public String getLocalDatabaseLastUpdate();

    public String getLastDailyResetTrigger();

    public String getLastFirebaseTrigger();

    public String getLastFirebaseUpdate();

    public String getLastFirebaseUpdateValue();

    public void updateLocalDatabase();

    public void updateFirebase();
}
