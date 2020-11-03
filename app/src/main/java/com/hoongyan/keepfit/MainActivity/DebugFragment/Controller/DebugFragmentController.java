package com.hoongyan.keepfit.MainActivity.DebugFragment.Controller;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.Workers.UpdateFireBaseWorker;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DebugFragmentController implements DebugFragmentControllerInterface{
    //MVC Component
    MVCModel mvcModel;

    //Data Field
    Context context;

    public DebugFragmentController(Context context) {
        mvcModel = new MVCModel(context, true);
        this.context = context;
    }

    public String getUserID(){
        return mvcModel.getUserID();
    }

    public String getSensorType(){
        return mvcModel.getSensorType();
    }

    public String getCurrentSteps(){
        return "" + (int) mvcModel.getCurrentSteps() + "";
    }

    public String getWorkerState(String workUniqueName) {
        WorkManager instance = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosForUniqueWork(workUniqueName);
        try {
            List<WorkInfo> workInfoList = statuses.get();
            int workListSize = workInfoList.size();

            if(workListSize > 0) {
                String result = "";

                for (int i = 0; i < workListSize; i++) {
                    if (i > 4) break;
                    result = result + workInfoList.get(i).getState();
                    if (i + 1 < workListSize) result = result + ", ";
                }
                return result;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getLocalDatabaseContent(){
        return mvcModel.getLocalDatabaseContent();
    }

    public String getLocalDatabaseLastUpdate(){
        return mvcModel.getLocalDatabaseLastUpdate();
    }

    public String getLastDailyResetTrigger(){
        return mvcModel.getLastDailyResetTrigger();
    }

    public String getLastFirebaseTrigger(){
        return mvcModel.getLastFirebaseTrigger();
    }

    public String getLastFirebaseUpdate(){
        return mvcModel.getLastFirebaseUpdate();
    }

    public String getLastFirebaseUpdateValue(){
        return mvcModel.getLastFirebaseUpdateValue();
    }

    public void updateLocalDatabase(){
        mvcModel.updateLocalDatabase(Integer.MIN_VALUE, null);
    }

    public void updateFirebase(){

        updateLocalDatabase();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest updateFirebaseWorkRequest = new OneTimeWorkRequest.Builder(UpdateFireBaseWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork("Debug_UpdateFirebase", ExistingWorkPolicy.REPLACE, updateFirebaseWorkRequest);
    }


}
