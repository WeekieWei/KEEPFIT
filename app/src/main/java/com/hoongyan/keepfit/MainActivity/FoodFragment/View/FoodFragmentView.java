package com.hoongyan.keepfit.MainActivity.FoodFragment.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.hoongyan.keepfit.JavaClass.CalorieSlotDataObject;
import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;
import com.hoongyan.keepfit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodFragmentView implements FoodFragmentViewInterface{

    //MVC Components
    private FoodFragmentController foodFragmentController;

    //Data Field
    private View rootView;
    private FragmentActivity activity;
    private Fragment fragment;
    private Map<Integer, JSONObject> foodMap;

    //Views
    CardView cardView;
    LinearLayout nlpLinearLayout, foodInputLinearLayout;
    EditText foodInputEditText;
    ImageButton voiceButton, proceedButton, clearButton;
    Button submitButton;
    TextView loadingTextView;

    CardView foodSlotCard, slot1Card, slot2Card, slot3Card, slot4Card, slot5Card, slot6Card;
    RadioButton slot1Button, slot2Button, slot3Button, slot4Button, slot5Button, slot6Button;
    TextView slot1Percent, slot2Percent, slot3Percent, slot4Percent, slot5Percent, slot6Percent;
    ProgressBar slot1ProgressBar, slot2ProgressBar, slot3ProgressBar, slot4ProgressBar, slot5ProgressBar, slot6ProgressBar;
    TextView slot1SmallText, slot2SmallText, slot3SmallText, slot4SmallText, slot5SmallText, slot6SmallText;


    public FoodFragmentView(Fragment fragment){
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        foodMap = new HashMap<>();
    }

    @Override
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_food, container, false);
        initializeViews();
        return rootView;
    }

    @Override
    public void setMVCController(FoodFragmentController mvcController){
        foodFragmentController = mvcController;
    }

    @Override
    public void nlpLayoutHide(){
        foodSlotCard.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);
        nlpLinearLayout.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.VISIBLE);
        foodInputLinearLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
    }

    @Override
    public void nlpLayoutReveal(){
        foodSlotCard.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);
        nlpLinearLayout.setVisibility(View.VISIBLE);
        foodInputEditText.setText("");
        foodInputLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyNLPProcessFinish(){
        loadingTextView.setVisibility(View.GONE);
        if(foodMap.size() < 1){
            nlpLayoutReveal();
            Toast.makeText(activity.getApplicationContext(), "No food detected", Toast.LENGTH_SHORT).show();
        }else{
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void createFoodItem(final int calorieViewID, JSONObject foodObject) throws JSONException {

        foodMap.put(calorieViewID, foodObject);

        final JSONObject foodInfo = foodMap.get(calorieViewID);

        final double defQty = foodInfo.getDouble("serving_qty");
        final double defGram = foodInfo.getDouble("grams");
        final double defCal = foodInfo.getDouble("calories");


        final LinearLayout foodLayout;
        GridLayout amountGrid, calorieGrid, buttonGrid;

        final TextView foodName, calorieTitle, calorieValue;
        final EditText foodQuantity;
        Button updateButton, deleteButton;



        foodLayout = new LinearLayout(activity);
        foodLayout.setBackgroundColor(Color.parseColor("#D7FFBD5C"));
        foodLayout.setOrientation(LinearLayout.VERTICAL);

        //1st Row
        foodName = new TextView(activity);
        foodName.setText(foodInfo.getString("food_name"));
        foodLayout.addView(foodName);

        //2nd Row

        amountGrid = new GridLayout(activity);
        amountGrid.setColumnCount(10);

        foodQuantity = new EditText(activity);
        foodQuantity.setText(foodInfo.getDouble("serving_qty") + "");
        foodQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        foodQuantity.setSelectAllOnFocus(true);
        foodQuantity.setMinimumWidth(10);
        foodQuantity.setMaxWidth(200);
//        foodQuantity.setLayoutParams(getGridParam(0, 1));

        final Spinner spinner = new Spinner(activity);
//        spinner.setLayoutParams(getGridParam(1,1));

        ArrayList<String> spinnerList = new ArrayList<>();

        JSONArray altMeasure = foodInfo.getJSONArray("alt_measures");
        String defaultUnit = foodInfo.getString("serving_unit");
        spinnerList.add(defaultUnit);
        for(int i = 0; i < altMeasure.length(); i++){
            String alt = altMeasure.getJSONObject(i).getString("measure");
            if(!defaultUnit.equals(alt)) spinnerList.add(alt);
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        amountGrid.addView(foodQuantity);
        amountGrid.addView(spinner);

        //3rd Row

        calorieGrid = new GridLayout(activity);
        calorieGrid.setColumnCount(2);

        calorieTitle = new TextView(activity);
        calorieTitle.setText("Calorie Gain: ");
        calorieTitle.setLayoutParams(getGridParam(0,3));

        calorieValue = new TextView(activity);
        calorieValue.setId(calorieViewID);
        calorieValue.setText(foodInfo.getDouble("calories") + "");
        calorieValue.setLayoutParams(getGridParam(1,7));

        calorieGrid.addView(calorieTitle);
        calorieGrid.addView(calorieValue);

        //4th Row
        buttonGrid = new GridLayout(activity);
        buttonGrid.setColumnCount(2);

        updateButton = new Button(activity);
        updateButton.setText("Update");
        updateButton.setLayoutParams(getGridParam(0,1));


        deleteButton = new Button(activity);
        deleteButton.setText("Delete");
        deleteButton.setLayoutParams(getGridParam(1,1));

        buttonGrid.addView(updateButton);
        buttonGrid.addView(deleteButton);

        foodLayout.addView(amountGrid);
        foodLayout.addView(calorieGrid);
        foodLayout.addView(buttonGrid);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double newQty = Double.parseDouble(foodQuantity.getText().toString());
                String newUnit = spinner.getSelectedItem().toString();
                try {
                    calorieValue.setText(getNewCalorie(foodInfo, newQty, newUnit) + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodInputLinearLayout.removeView(foodLayout);
                foodMap.remove(calorieViewID);
                if(foodMap.size() == 0){
                    nlpLayoutReveal();
                }
            }
        });



        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                foodInputLinearLayout.addView(foodLayout, foodInputLinearLayout.getChildCount() - 1);
                // Stuff that updates the UI

            }
        });
    }

    private double getNewCalorie(JSONObject foodInfo, double newQty, String newUnit) throws JSONException {
        final double defQty = foodInfo.getDouble("serving_qty");
        final double defGram = foodInfo.getDouble("grams");
        final double defCal = foodInfo.getDouble("calories");

        double newGram = -1;

        JSONArray altMeasure = foodInfo.getJSONArray("alt_measures");
        for(int i = 0; i < altMeasure.length(); i++){
            String alt = altMeasure.getJSONObject(i).getString("measure");

            if(newUnit.equals(alt)){
                newGram = altMeasure.getJSONObject(i).getDouble("serving_weight")/altMeasure.getJSONObject(i).getDouble("qty");
                break;
            }
        }
        Log.i("VAR", "defQty: " + defQty + "\ndefGram: " + defGram
                + "\ndefCal: " + defCal + "\nnewGram: " + newGram + "\nnewQty: " + newQty);

        return newQty*(newGram/defGram*defCal);
    }


    private GridLayout.LayoutParams getGridParam(int col, int weight){
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(col, (float) weight);

        return params;
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initializeViews() {
        loadingTextView = rootView.findViewById(R.id.loadingTextView);
        cardView = rootView.findViewById(R.id.nlpCardView);
        nlpLinearLayout = rootView.findViewById(R.id.nlpLinearLayout);
        foodInputLinearLayout = rootView.findViewById(R.id.foodInputLinearLayout);
        foodInputEditText = rootView.findViewById(R.id.nlpCardInputEditText);
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
                foodInputEditText.setText("");
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
                    fragment.startActivityForResult(intent, 1000);
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

                String foodInputText = foodInputEditText.getText().toString();
                if(foodInputText.isEmpty()){
                    Toast.makeText(rootView.getContext(), "Input something", Toast.LENGTH_SHORT).show();
                }else if(!foodFragmentController.isOnline()){
                    Toast.makeText(rootView.getContext(), "ERROR!! No Internet Connection", Toast.LENGTH_SHORT).show();
                }else{
                    foodFragmentController.processNLP(foodInputText);
                }
            }
        });

        submitButton = rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totalCal = 0;
                String mealName = "";
                int foodCount = foodMap.size(), i=0;
                Log.i("Food map Size", foodMap.size() + "");

                for(int key : foodMap.keySet()){
                    TextView cal = rootView.findViewById(key);
                    totalCal += Double.parseDouble(cal.getText().toString());
                    try {
                        mealName += foodMap.get(key).getString("food_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    i++;
                    if (i < foodCount) {
                        if(i == foodCount - 1){
                            mealName += " & ";
                        }else{
                            mealName += ", ";
                        }
                    }
                }
                submitButton.setEnabled(false);
                loadingTextView.setVisibility(View.VISIBLE);
                foodFragmentController.addMealToFirebase(mealName, totalCal, getSelectedRadioButtonSlot());
            }
        });

        foodSlotCard = rootView.findViewById(R.id.foodSlotCard);
        foodSlotCard.setVisibility(View.VISIBLE);

        slot1Card = rootView.findViewById(R.id.slot1Card);
        slot1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(1);
            }
        });
        slot1Button = rootView.findViewById(R.id.slot1Button);
        slot1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(1);
            }
        });

        slot2Card = rootView.findViewById(R.id.slot2Card);
        slot2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(2);
            }
        });
        slot2Button = rootView.findViewById(R.id.slot2Button);
        slot2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(2);
            }
        });

        slot3Card = rootView.findViewById(R.id.slot3Card);
        slot3Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(3);
            }
        });
        slot3Button = rootView.findViewById(R.id.slot3Button);
        slot3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(3);
            }
        });

        slot4Card = rootView.findViewById(R.id.slot4Card);
        slot4Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(4);
            }
        });
        slot4Button = rootView.findViewById(R.id.slot4Button);
        slot4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(4);
            }
        });

        slot5Card = rootView.findViewById(R.id.slot5Card);
        slot5Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(5);
            }
        });
        slot5Button = rootView.findViewById(R.id.slot5Button);
        slot5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(5);
            }
        });

        slot6Card = rootView.findViewById(R.id.slot6Card);
        slot6Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(6);
            }
        });
        slot6Button = rootView.findViewById(R.id.slot6Button);
        slot6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRadioButton(6);
            }
        });

        slot1Percent = rootView.findViewById(R.id.slot1Percent);
        slot2Percent = rootView.findViewById(R.id.slot2Percent);
        slot3Percent = rootView.findViewById(R.id.slot3Percent);
        slot4Percent = rootView.findViewById(R.id.slot4Percent);
        slot5Percent = rootView.findViewById(R.id.slot5Percent);
        slot6Percent = rootView.findViewById(R.id.slot6Percent);

        slot1SmallText = rootView.findViewById(R.id.slot1SmallText);
        slot2SmallText = rootView.findViewById(R.id.slot2SmallText);
        slot3SmallText = rootView.findViewById(R.id.slot3SmallText);
        slot4SmallText = rootView.findViewById(R.id.slot4SmallText);
        slot5SmallText = rootView.findViewById(R.id.slot5SmallText);
        slot6SmallText = rootView.findViewById(R.id.slot6SmallText);

        slot1ProgressBar = rootView.findViewById(R.id.slot1ProgressBar);
        slot2ProgressBar = rootView.findViewById(R.id.slot2ProgressBar);
        slot3ProgressBar = rootView.findViewById(R.id.slot3ProgressBar);
        slot4ProgressBar = rootView.findViewById(R.id.slot4ProgressBar);
        slot5ProgressBar = rootView.findViewById(R.id.slot5ProgressBar);
        slot6ProgressBar = rootView.findViewById(R.id.slot6ProgressBar);

        foodFragmentController.requestUpdateCalSlots();
    }

    public void updateCalSlotsData(CalorieSlotDataObject object, int selectedSlotIndex){
        float slot1Suggested = object.getSlot1Suggested();
        float slot2Suggested = object.getSlot2Suggested();
        float slot3Suggested = object.getSlot3Suggested();
        float slot4Suggested = object.getSlot4Suggested();
        float slot5Suggested = object.getSlot5Suggested();
        float slot6Suggested = object.getSlot6Suggested();
        float slot1Taken = object.getSlot1Taken();
        float slot2Taken = object.getSlot2Taken();
        float slot3Taken = object.getSlot3Taken();
        float slot4Taken = object.getSlot4Taken();
        float slot5Taken = object.getSlot5Taken();
        float slot6Taken = object.getSlot6Taken();

        String text1 = (int) slot1Taken + "/" + (int) slot1Suggested + " cal";
        String text2 = (int) slot2Taken + "/" + (int) slot2Suggested + " cal";
        String text3 = (int) slot3Taken + "/" + (int) slot3Suggested + " cal";
        String text4 = (int) slot4Taken + "/" + (int) slot4Suggested + " cal";
        String text5 = (int) slot5Taken + "/" + (int) slot5Suggested + " cal";
        String text6 = (int) slot6Taken + "/" + (int) slot6Suggested + " cal";
        slot1SmallText.setText(text1);
        slot2SmallText.setText(text2);
        slot3SmallText.setText(text3);
        slot4SmallText.setText(text4);
        slot5SmallText.setText(text5);
        slot6SmallText.setText(text6);

        float slot1Percent = slot1Taken / slot1Suggested * 100;
        float slot2Percent = slot2Taken / slot2Suggested * 100;
        float slot3Percent = slot3Taken / slot3Suggested * 100;
        float slot4Percent = slot4Taken / slot4Suggested * 100;
        float slot5Percent = slot5Taken / slot5Suggested * 100;
        float slot6Percent = slot6Taken / slot6Suggested * 100;

        this.slot1Percent.setText((int) slot1Percent + "%");
        this.slot2Percent.setText((int) slot2Percent + "%");
        this.slot3Percent.setText((int) slot3Percent + "%");
        this.slot4Percent.setText((int) slot4Percent + "%");
        this.slot5Percent.setText((int) slot5Percent + "%");
        this.slot6Percent.setText((int) slot6Percent + "%");

        slot1ProgressBar.setProgress((int) slot1Percent, true);
        slot2ProgressBar.setProgress((int) slot2Percent, true);
        slot3ProgressBar.setProgress((int) slot3Percent, true);
        slot4ProgressBar.setProgress((int) slot4Percent, true);
        slot5ProgressBar.setProgress((int) slot5Percent, true);
        slot6ProgressBar.setProgress((int) slot6Percent, true);

        checkRadioButton(selectedSlotIndex);

    }

    private int getSelectedRadioButtonSlot(){
        if(slot1Button.isChecked())
            return 1;
        else if(slot2Button.isChecked())
            return 2;
        else if(slot3Button.isChecked())
            return 3;
        else if(slot4Button.isChecked())
            return 4;
        else if(slot5Button.isChecked())
            return 5;
        else if(slot6Button.isChecked())
            return 6;
        else
            return 0;
    }

    private void checkRadioButton(int slotButtonIndex) {
        slot1Button.setChecked(false);
        slot2Button.setChecked(false);
        slot3Button.setChecked(false);
        slot4Button.setChecked(false);
        slot5Button.setChecked(false);
        slot6Button.setChecked(false);

        switch (slotButtonIndex) {
            case 1:
                slot1Button.setChecked(true);
                break;
            case 2:
                slot2Button.setChecked(true);
                break;
            case 3:
                slot3Button.setChecked(true);
                break;
            case 4:
                slot4Button.setChecked(true);
                break;
            case 5:
                slot5Button.setChecked(true);
                break;
            case 6:
                slot6Button.setChecked(true);
                break;
            default:
        }
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
    public void onActivityResult(Intent data){
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        String txt = foodInputEditText.getText().toString();

        if(txt.length() == 0)
            txt += result.get(0);
        else
            txt += " " + result.get(0);
        foodInputEditText.setText(txt);
    }

    @Override
    public void notifyFirebaseUpdate(boolean status){
        if(status){
            nlpLayoutReveal();
            Toast.makeText(activity.getApplicationContext(), "Meal is added", Toast.LENGTH_SHORT).show();
            foodInputLinearLayout.removeViewsInLayout(0, foodMap.size());
            foodMap.clear();
            foodFragmentController.requestUpdateCalSlots();
        }else{
            Toast.makeText(activity.getApplicationContext(), "ERROR!! No internet connection", Toast.LENGTH_SHORT).show();
        }
        submitButton.setEnabled(true);
        loadingTextView.setVisibility(View.GONE);
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
