package com.hoongyan.keepfit.MainActivity.FoodFragment.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.FoodFragment.View.FoodFragmentView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodFragmentController implements FoodFragmentControllerInterface{

    SharedPreferences sharedPreferences;

    //MVC Components
    private FoodFragmentView foodFragmentView;
    private MVCModel mvcModel;

    //Data Field
    private boolean isFoodCalorieTaskRunning;
    //private Map<Integer, JSONObject> foodMap;

    public FoodFragmentController(Context context, FoodFragmentView foodFragmentView){
        this.foodFragmentView = foodFragmentView;
        mvcModel = new MVCModel(context, true);
        sharedPreferences = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE);
    }

    @Override
    public void processNLP(String naturalLanguageText){
        if(!isFoodCalorieTaskRunning) new FoodCalorieTask().execute(naturalLanguageText);
    }


    private class FoodCalorieTask extends AsyncTask<String, Void, Void>{

        private int foodCount = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isFoodCalorieTaskRunning = true;
            foodFragmentView.nlpLayoutHide();
        }

        @Override
        protected Void doInBackground(String... inputs) {

            JSONObject inputJson = new JSONObject();
            try {
                inputJson.put("query", inputs[0]);
                inputJson.put("locale", "en_US");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(inputJson.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url("https://trackapi.nutritionix.com/v2/natural/nutrients")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-app-id", "b1daa705")
                    .addHeader("x-app-key",
                            "b84a2813785c06d233696008eddc7904")
                    .addHeader("x-remote-user-id", "0")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String a = response.body().string();
//            String a = sharedPreferences.getString("response", "");
                JSONObject responseJson = new JSONObject(a);
//            sharedPreferences.edit().putString("response", a).apply();
                JSONArray array = responseJson.getJSONArray("foods");

                foodCount = array.length();

                for(int i=0; i < foodCount; i++) {
                    JSONObject foodObject = new JSONObject();
                    foodObject.put("food_name", array.getJSONObject(i).getString("food_name"));
                    foodObject.put("serving_qty", array.getJSONObject(i).getDouble("serving_qty") + "");
                    foodObject.put("serving_unit", array.getJSONObject(i).getString("serving_unit"));
                    foodObject.put("grams", array.getJSONObject(i).getDouble("serving_weight_grams") + "");
                    foodObject.put("calories", array.getJSONObject(i).getDouble("nf_calories") + "");


//                Log.i("FOOD " + (i+1), array.getJSONObject(i).getString("food_name"));
//                Log.i("Quantity", array.getJSONObject(i).getDouble("serving_qty") + "");
//                Log.i("Unit", array.getJSONObject(i).getString("serving_unit"));
//                Log.i("Gram", array.getJSONObject(i).getDouble("serving_weight_grams") + "");
//                Log.i("Calorie", array.getJSONObject(i).getDouble("nf_calories") + "");

                    JSONArray altMeasures = array.getJSONObject(i).getJSONArray("alt_measures");
                    JSONArray foodMeasureArray = new JSONArray();

                    for (int j = 0; j < altMeasures.length(); j++) {
                        JSONObject foodMeasureObject = new JSONObject();
                        foodMeasureObject.put("serving_weight", altMeasures.getJSONObject(j).getDouble("serving_weight") + "");
                        foodMeasureObject.put("measure", altMeasures.getJSONObject(j).getString("measure"));
                        foodMeasureObject.put("qty", altMeasures.getJSONObject(j).getDouble("qty") + "");

                        foodMeasureArray.put(j, foodMeasureObject);

//                    Log.d("weight", altMeasures.getJSONObject(j).getDouble("serving_weight") + "");
//                    Log.d("measure", altMeasures.getJSONObject(j).getString("measure"));
//                    Log.d("qty", altMeasures.getJSONObject(j).getDouble("qty") + "");
                    }

                    foodObject.put("alt_measures", foodMeasureArray);


                    int calorieViewId = ViewGroup.generateViewId();

//                    foodMap.put(calorieViewId, foodObject);

                    foodFragmentView.createFoodItem(calorieViewId, foodObject);
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
            isFoodCalorieTaskRunning = false;
            foodFragmentView.notifyNLPProcessFinish();
        }
    }

    @Override
    public void addMealToFirebase(String mealName, double totalCal){

        if(!isOnline()){
            foodFragmentView.notifyFirebaseUpdate(false);
            return;
        }

        mvcModel.updateFood(new MVCModel.TaskResultStatus() {
            @Override
            public void onResultReturn(boolean result) {
                foodFragmentView.notifyFirebaseUpdate(result);
            }
        }, mealName, totalCal);
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
