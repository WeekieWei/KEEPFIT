package com.hoongyan.keepfit.MainActivity.FoodFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;
import com.hoongyan.keepfit.MainActivity.FoodFragment.View.FoodFragmentView;
import com.hoongyan.keepfit.R;

import static android.app.Activity.RESULT_OK;


public class FoodFragment extends Fragment {

    //MVC Components
    private FoodFragmentView foodFragmentView;
    private FoodFragmentController foodFragmentController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foodFragmentView = new FoodFragmentView(FoodFragment.this);
        foodFragmentController = new FoodFragmentController(getActivity().getApplicationContext(), foodFragmentView);
        foodFragmentView.setMVCController(foodFragmentController);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return foodFragmentView.inflateFragmentLayout(inflater, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == RESULT_OK && data != null){

                foodFragmentView.onActivityResult(data);
            }
        }
    }
}