package com.hoongyan.keepfit.MainActivity.HomeFragment.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hoongyan.keepfit.JavaClass.UserHomePageData;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.HomeFragment.Controller.HomeFragmentController;
import com.hoongyan.keepfit.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragmentView implements HomeFragmentViewInterface {

    //MVC Components
    private HomeFragmentController homeFragmentController;

    //Data Field
    private View rootView;
    private FragmentActivity activity;
    private Fragment fragment;

    private Handler handler;
    private Runnable runnable;

    //Views
    private TextView card1Title, card1TimeText, card2_1Text, card2_2Text, card2_3Text, card2_4Text
            , card2_5Text, card3Text, card4_1Text;
    private Button card4_1Button;
    private ImageView card1Image;
    private BarChart card3Chart;
    private CombinedChart card4Chart;
    private CardView card3, card4, card4_1;

    public HomeFragmentView(Fragment homeFragment){
        this.fragment = homeFragment;
        activity = homeFragment.getActivity();
        handler = new Handler();
    }

    @Override
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        return rootView;
    }

    @Override
    public void setMVCController(HomeFragmentController controller){
        homeFragmentController = controller;
    }

    @Override
    public void initializeViews() {
        card1Title = rootView.findViewById(R.id.card1Title);
        card1TimeText = rootView.findViewById(R.id.card1TimeText);
        card2_1Text = rootView.findViewById(R.id.card2_1Text);
        card2_2Text = rootView.findViewById(R.id.card2_2Text);
        card2_3Text = rootView.findViewById(R.id.card2_3Text);
        card2_4Text = rootView.findViewById(R.id.card2_4Text);
        card2_5Text = rootView.findViewById(R.id.card2_5Text);
        card1Image = rootView.findViewById(R.id.card1Image);
        card3 = rootView.findViewById(R.id.card3);

        card3Chart = rootView.findViewById(R.id.card3Chart);
        XAxis xAxis = card3Chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(10f);
        card3Chart.getDescription().setEnabled(false);
        card3Chart.setTouchEnabled(false);

        card3Text = rootView.findViewById(R.id.card3Text);

        card4 = rootView.findViewById(R.id.card4);
        card4_1 = rootView.findViewById(R.id.card4_1);
        card4_1Text = rootView.findViewById(R.id.card4_1Text);
        card4_1Button = rootView.findViewById(R.id.card4_1Button);

        card4_1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                builder.setTitle("Update Weight");
                View viewInflated = LayoutInflater.from(fragment.getContext()).inflate(R.layout.layout_input_meal_name, (ViewGroup) fragment.getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!homeFragmentController.updateWeight(Float.parseFloat(input.getText().toString()))){
                            Toast.makeText(fragment.getContext(), "Can't update without internet connection", Toast.LENGTH_SHORT).show();
                        }else{
                            card4_1.setVisibility(View.GONE);
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        card4Chart = rootView.findViewById(R.id.card4Chart);
        XAxis xAxis2 = card4Chart.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setLabelRotationAngle(10f);
//        card4Chart.getAxisLeft().setAxisMinimum(0f);
        card4Chart.getDescription().setEnabled(false);

        homeFragmentController.getUpdatedHomePageData();

    }

    @Override
    public void notifyUpdateWeightSuccess(float bmi, float weightAdjust){

        AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext()).create();

        String title = "Weight Update Success";

        String message = "Your new BMI is " + String.format("%.1f", bmi);

        if (weightAdjust < 0) {
            message += " (Overweight)";
        } else if (weightAdjust > 0){
            message += " (Underweight)";
        }else {
            message += " (Healthy Weight)";
        }

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (DialogInterface.OnClickListener) null);
        alertDialog.show();
    }

    @Override
    public void onPause(){
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume(){
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                homeFragmentController.getUpdatedHomePageData();
                handler.postDelayed(runnable, 1000);
            }
        }, 1000);
    }

    @Override
    public void bindDataToViews(UserHomePageData data){
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                card1Title.setText(data.getCard1Title());
                card1Image.setImageResource(data.getCard1ImageID());
                card1TimeText.setText(data.getCard1TimeText());
                card2_1Text.setText(data.getCard2_1Text());
                card2_2Text.setText(data.getCard2_2Text());
                card2_3Text.setText(data.getCard2_3Text());
                card2_4Text.setText(data.getCard2_4Text());
                card2_5Text.setText(data.getCard2_5Text());
            }
        });
    }

    @Override
    public void bindDataToCard3Chart(BarData barData, ArrayList<String> xAxisLabel, float avgNet, boolean isNotEmpty){
        try {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isNotEmpty) {
                        card3.setVisibility(View.VISIBLE);
                        card3Chart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                return xAxisLabel.get((int) value);
                            }
                        });
                        card3Chart.setData(barData);
                        card3Chart.setFitBars(true);
                        card3Chart.animateXY(2000, 5000);
                        card3Chart.invalidate();

                        String card3Text = "Your average net calories is " + String.format("%.2f", avgNet)
                                + " cal, ";

                        if (Math.abs(avgNet) < 200) {
                            card3Text += "which is about right, keep going the same lifestyle.";
                        } else if (avgNet < 0) {
                            card3Text += "which means you generally do not have enough calorie to stay healthy every day, eat more or do less extensive exercise.";
                        } else {
                            card3Text += "which means you generally have extra calorie every day, eat lesser or do more extensive exercise.";
                        }

                        HomeFragmentView.this.card3Text.setText(card3Text);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void bindDataToCard4Chart(CombinedData combinedData, ArrayList<String> xAxisLabel, float weight, boolean isNotEmpty){
        try {


            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isNotEmpty) {
                        card4.setVisibility(View.VISIBLE);

                        if (weight != -1) {
                            card4_1.setVisibility(View.VISIBLE);
                            card4_1Text.setText("Current Weight: " + String.format("%.2f", weight) + " kg");
                        }


                        card4Chart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                return xAxisLabel.get((int) value);
                            }
                        });
                        card4Chart.setData(combinedData);
                        card4Chart.animateXY(2000, 5000);
                        card4Chart.invalidate();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

}
