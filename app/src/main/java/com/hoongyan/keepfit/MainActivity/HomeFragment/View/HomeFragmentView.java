package com.hoongyan.keepfit.MainActivity.HomeFragment.View;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hoongyan.keepfit.JavaClass.UserHomePageData;
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
    TextView card1Title, card1TimeText, card2_1Text, card2_2Text, card2_3Text, card2_4Text
            , card2_5Text;
    ImageView card1Image;
    BarChart card3Chart;
    CardView card3;

    public HomeFragmentView(Fragment homeFragment){
        this.fragment = homeFragment;
        activity = homeFragment.getActivity();
        handler = new Handler();
    }

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        return rootView;
    }

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

        homeFragmentController.getUpdatedHomePageData();

    }

    public void onPause(){
        handler.removeCallbacks(runnable);
    }

    public void onResume(){
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                homeFragmentController.getUpdatedHomePageData();
                handler.postDelayed(runnable, 1000);
            }
        }, 1000);
    }

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

    public void bindDataToCard3Chart(BarData barData, ArrayList<String> xAxisLabel){
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                card3.setVisibility(View.VISIBLE);
                card3Chart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
//                        return super.getAxisLabel(value, axis);
                        return xAxisLabel.get((int) value);
                    }
                });
                card3Chart.setData(barData);
                card3Chart.setFitBars(true);
                card3Chart.animateXY(2000, 2000);
                card3Chart.invalidate();
            }
        });
    }

    @Override
    public View getRootView() {
        return rootView;
    }

}
