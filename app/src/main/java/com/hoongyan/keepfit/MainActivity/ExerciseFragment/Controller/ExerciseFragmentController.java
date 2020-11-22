package com.hoongyan.keepfit.MainActivity.ExerciseFragment.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hoongyan.keepfit.JavaClass.ExerciseObject;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.ExerciseFragment.ExerciseRecycleViewAdapter;
import com.hoongyan.keepfit.MainActivity.ExerciseFragment.View.ExerciseFragmentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExerciseFragmentController implements ExerciseFragmentFragmentControllerInterface{
    SharedPreferences sharedPreferences;

    //MVC Components
    private ExerciseFragmentView exerciseFragmentView;
    private MVCModel mvcModel;

    //Data Field
    private boolean isExerciseTaskRunning;
    private ArrayList<ExerciseObject> exerciseList;
    //private Map<Integer, JSONObject> foodMap;


    public ExerciseFragmentController(Context context, ExerciseFragmentView exerciseFragmentView){
        this.exerciseFragmentView = exerciseFragmentView;
        mvcModel = new MVCModel(context, true);
        sharedPreferences = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE);
    }

    @Override
    public void processNLP(String naturalLanguageText) {
        if(!isExerciseTaskRunning) new ExerciseTask().execute(naturalLanguageText);
    }

    private class ExerciseTask extends AsyncTask<String, Void, Void> {

        private int exerciseCount = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isExerciseTaskRunning = true;
            exerciseFragmentView.nlpLayoutHide();
        }

        @Override
        protected Void doInBackground(String... inputs) {

            JSONObject inputJson = new JSONObject();
            try {
                inputJson.put("query", inputs[0]);
                inputJson.put("gender", mvcModel.getUserGender());
                inputJson.put("weight_kg", mvcModel.getUserWeight());
                inputJson.put("height_cm", mvcModel.getUserHeight());
                inputJson.put("age", mvcModel.getUserAge());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(inputJson.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url("https://trackapi.nutritionix.com/v2/natural/exercise")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-app-id", "b1daa705")
                    .addHeader("x-app-key",
                            "b84a2813785c06d233696008eddc7904")
                    .addHeader("x-remote-user-id", "0")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String b = response.body().string();
//            String b = sharedPreferences.getString("responseB", "");
                JSONObject responseJson = new JSONObject(b);
            sharedPreferences.edit().putString("responseB", b).apply();
                JSONArray array = responseJson.getJSONArray("exercises");

                exerciseCount = array.length();


                for(int i = 0; i < exerciseCount; i++) {
                    JSONObject exerciseObject = new JSONObject();
                    exerciseObject.put("name", array.getJSONObject(i).getString("name"));
                    exerciseObject.put("met", array.getJSONObject(i).getDouble("met") + "");
                    exerciseObject.put("duration", array.getJSONObject(i).getDouble("duration_min") + "");
                    exerciseObject.put("calories", array.getJSONObject(i).getDouble("nf_calories") + "");

//                    Log.i("name", array.getJSONObject(i).getString("name"));
//                    Log.i("met", array.getJSONObject(i).getDouble("met") + "");
//                    Log.i("duration", array.getJSONObject(i).getDouble("duration_min") + "");
//                    Log.i("calories", array.getJSONObject(i).getDouble("nf_calories") + "");

                    int calorieViewId = ViewGroup.generateViewId();


                    exerciseFragmentView.createFoodItem(calorieViewId, exerciseObject);
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isExerciseTaskRunning = false;
            exerciseFragmentView.notifyNLPProcessFinish();
        }
    }

    public RecyclerView.Adapter getRecycleViewAdapter(){

        exerciseList = new ArrayList<>();
        exerciseList.add(new ExerciseObject("Jogging; General", 7.0));
        exerciseList.add(new ExerciseObject("Rope Skipping; General", 12.3));
        exerciseList.add(new ExerciseObject("Bicycling; Moderate effort", 8.0));
        exerciseList.add(new ExerciseObject("Bicycling; Vigorous effort", 14.0));
        exerciseList.add(new ExerciseObject("Hatha Yoga", 2.5));
        exerciseList.add(new ExerciseObject("Basketball; General", 6.5));
        exerciseList.add(new ExerciseObject("Tennis: Singles", 8.0));
        exerciseList.add(new ExerciseObject("Hiking", 7.3));
        exerciseList.add(new ExerciseObject("Standing", 1.8));

        return new ExerciseRecycleViewAdapter(exerciseList, mvcModel.getUserWeightFloat());
    }

    public double getCalVal(double met){
        return met * 3.5 * mvcModel.getUserWeightFloat() / 200;
    }

    @Override
    public void addMealToFirebase(String exerciseName, double totalCal) {
        if(!isOnline()){
            exerciseFragmentView.notifyFirebaseUpdate(false);
            return;
        }

        mvcModel.updateExercise(new MVCModel.TaskResultStatus() {
            @Override
            public void onResultReturn(boolean result) {
                exerciseFragmentView.notifyFirebaseUpdate(result);
            }
        }, exerciseName, totalCal);
    }

    @Override
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
