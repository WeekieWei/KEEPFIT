package com.hoongyan.keepfit.MainActivity.ExerciseFragment.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.hoongyan.keepfit.MainActivity.ExerciseFragment.Controller.ExerciseFragmentController;
import com.hoongyan.keepfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExerciseFragmentView implements ExerciseFragmentViewInterface{
    //MVC Components
    private ExerciseFragmentController exerciseFragmentController;

    //Data Field
    private View rootView;
    private FragmentActivity activity;
    private Fragment fragment;
    private Map<Integer, JSONObject> exerciseMap;

    //Views
    LinearLayout nlpLinearLayout, exerciseInputLinearLayout;
    EditText exerciseInputEditText;
    ImageButton voiceButton, proceedButton, clearButton;
    Button submitButton;
    TextView loadingTextView;

    public ExerciseFragmentView(Fragment fragment){
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        exerciseMap = new HashMap<>();
    }

    @Override
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_food, container, false);
        initializeViews();
        return rootView;
    }

    @Override
    public void setMVCController(ExerciseFragmentController mvcController) {
        exerciseFragmentController = mvcController;
    }

    @Override
    public void nlpLayoutHide() {
        nlpLinearLayout.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.VISIBLE);
        exerciseInputLinearLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
    }

    @Override
    public void nlpLayoutReveal() {
        nlpLinearLayout.setVisibility(View.VISIBLE);
        exerciseInputEditText.setText("");
        exerciseInputLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyNLPProcessFinish() {
        loadingTextView.setVisibility(View.GONE);
        if(exerciseMap.size() < 1){
            nlpLayoutReveal();
            Toast.makeText(activity.getApplicationContext(), "No activity detected", Toast.LENGTH_SHORT).show();
        }else{
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void createFoodItem(int calorieViewID, JSONObject exerciseObject) throws JSONException {
        exerciseMap.put(calorieViewID, exerciseObject);

        final JSONObject exerciseInfo = exerciseMap.get(calorieViewID);

        final LinearLayout exerciseLayout;
        GridLayout amountGrid, calorieGrid, buttonGrid;

        final TextView exerciseName, metValue, calorieTitle, calorieValue, durationText;
        final EditText durationVal;
        Button updateButton, deleteButton;



        exerciseLayout = new LinearLayout(activity);
        exerciseLayout.setOrientation(LinearLayout.VERTICAL);

        //1st Row
        exerciseName = new TextView(activity);
        exerciseName.setText(exerciseInfo.getString("name"));
        exerciseLayout.addView(exerciseName);

        //1.5 Row
        metValue = new TextView(activity);
        metValue.setText("MET: " + exerciseInfo.getDouble("met"));
        exerciseLayout.addView(metValue);

        //2nd Row

        amountGrid = new GridLayout(activity);
        amountGrid.setColumnCount(10);

        durationText = new TextView(activity);
        durationText.setText("Duration (min): ");
        durationText.setMinimumWidth(100);

        durationVal = new EditText(activity);
        durationVal.setText(exerciseInfo.getDouble("duration") + "");
        durationVal.setInputType(InputType.TYPE_CLASS_NUMBER);
        durationVal.setSelectAllOnFocus(true);
        durationVal.setMinimumWidth(10);
        durationVal.setMaxWidth(200);

        amountGrid.addView(durationText);
        amountGrid.addView(durationVal);

        //3rd Row

        calorieGrid = new GridLayout(activity);
        calorieGrid.setColumnCount(2);

        calorieTitle = new TextView(activity);
        calorieTitle.setText("Calorie Burnt: ");

        calorieValue = new TextView(activity);
        calorieValue.setId(calorieViewID);
        calorieValue.setText(exerciseInfo.getDouble("calories") + "");

        calorieGrid.addView(calorieTitle);
        calorieGrid.addView(calorieValue);

        //4th Row
        buttonGrid = new GridLayout(activity);
        buttonGrid.setColumnCount(2);

        updateButton = new Button(activity);
        updateButton.setText("Update");


        deleteButton = new Button(activity);
        deleteButton.setText("Delete");

        buttonGrid.addView(updateButton);
        buttonGrid.addView(deleteButton);

        exerciseLayout.addView(amountGrid);
        exerciseLayout.addView(calorieGrid);
        exerciseLayout.addView(buttonGrid);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double newDuration = Double.parseDouble(durationVal.getText().toString());
                try {
                    calorieValue.setText(getNewCalorie(exerciseInfo, newDuration) + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseInputLinearLayout.removeView(exerciseLayout);
                exerciseMap.remove(calorieViewID);
                if(exerciseMap.size() == 0){
                    nlpLayoutReveal();
                }
            }
        });



        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                exerciseInputLinearLayout.addView(exerciseLayout, exerciseInputLinearLayout.getChildCount() - 1);
                // Stuff that updates the UI

            }
        });
    }

    private double getNewCalorie(JSONObject exerciseInfo, double newDuration) throws JSONException {

        final double defDuration = exerciseInfo.getDouble("duration");
        final double defCal = exerciseInfo.getDouble("calories");

        return defCal*newDuration/defDuration;
    }

    @Override
    public void onActivityResult(Intent data) {
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        String txt = exerciseInputEditText.getText().toString();

        if(txt.length() == 0)
            txt += result.get(0);
        else
            txt += " " + result.get(0);
        exerciseInputEditText.setText(txt);
    }

    @Override
    public void notifyFirebaseUpdate(boolean status) {
        if(status){
            nlpLayoutReveal();
            Toast.makeText(activity.getApplicationContext(), "Exercise is added", Toast.LENGTH_SHORT).show();
            exerciseInputLinearLayout.removeViewsInLayout(0, exerciseMap.size());
            exerciseMap.clear();
        }else{
            Toast.makeText(activity.getApplicationContext(), "ERROR!! No internet connection", Toast.LENGTH_SHORT).show();
        }
        submitButton.setEnabled(true);
        loadingTextView.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initializeViews() {
        loadingTextView = rootView.findViewById(R.id.loadingTextView);
        nlpLinearLayout = rootView.findViewById(R.id.nlpLinearLayout);
        exerciseInputLinearLayout = rootView.findViewById(R.id.foodInputLinearLayout);
        exerciseInputEditText = rootView.findViewById(R.id.foodInputEditText);
        exerciseInputEditText.setHint("Tell us what exercise you have done:\n\nExample: I jogged for 30 minutes, and walked for 10 minutes");
        clearButton = rootView.findViewById(R.id.clearButton);
        clearButton.setOnTouchListener(new View.OnTouchListener() {
            private float pressedX = 0;
            private float pressedY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_UP :
                        clearButton.clearColorFilter();
                        if(distance(pressedX, pressedY, event.getX(), event.getY()) < 10)
                            v.performClick();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        clearButton.setColorFilter(android.R.color.holo_orange_light);
                        pressedX = event.getX();
                        pressedY = event.getY();
                        break;
                }
                return true;
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseInputEditText.setText("");
            }
        });


        voiceButton = rootView.findViewById(R.id.voiceButton);
        voiceButton.setOnTouchListener(new View.OnTouchListener() {
            private float pressedX = 0;
            private float pressedY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_UP :
                        voiceButton.clearColorFilter();
                        if(distance(pressedX, pressedY, event.getX(), event.getY()) < 10)
                            v.performClick();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        voiceButton.setColorFilter(android.R.color.holo_orange_light);
                        pressedX = event.getX();
                        pressedY = event.getY();
                        break;
                }
                return true;
            }
        });
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-MY");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something");

                try {
                    fragment.startActivityForResult(intent, 2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        proceedButton = rootView.findViewById(R.id.proceedButton);
        proceedButton.setOnTouchListener(new View.OnTouchListener() {
            private float pressedX = 0;
            private float pressedY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP :
                        proceedButton.clearColorFilter();
                        if(distance(pressedX, pressedY, event.getX(), event.getY()) < 10)
                            v.performClick();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        proceedButton.setColorFilter(android.R.color.holo_orange_light);
                        pressedX = event.getX();
                        pressedY = event.getY();
                        break;
                }
                return true;
            }
        });
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String foodInputText = exerciseInputEditText.getText().toString();
                if(foodInputText.isEmpty()){
                    Toast.makeText(rootView.getContext(), "Input something", Toast.LENGTH_SHORT).show();
                }else if(!exerciseFragmentController.isOnline()){
                    Toast.makeText(rootView.getContext(), "ERROR!! No Internet Connection", Toast.LENGTH_SHORT).show();
                }else{
                    exerciseFragmentController.processNLP(foodInputText);
                }
            }
        });

        submitButton = rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totalCal = 0;
                String exerciseName = "";
                int exerciseCount = exerciseMap.size(), i=0;
                Log.i("Exercise map Size", exerciseMap.size() + "");

                for(int key : exerciseMap.keySet()){
                    TextView cal = rootView.findViewById(key);
                    totalCal += Double.parseDouble(cal.getText().toString());
                    try {
                        exerciseName += exerciseMap.get(key).getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    i++;
                    if (i < exerciseCount) {
                        if(i == exerciseCount - 1){
                            exerciseName += " & ";
                        }else{
                            exerciseName += ", ";
                        }
                    }
                }
                submitButton.setEnabled(false);
                loadingTextView.setVisibility(View.VISIBLE);
                exerciseFragmentController.addMealToFirebase(exerciseName, totalCal);
            }
        });
    }

    private float distance(float x1, float y1, float x2, float y2) {
        Log.i("VALUE", x1 + " " + x2 + " " + y1 + " " + y2);
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / this.fragment.getResources().getDisplayMetrics().density;
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
