package com.hoongyan.keepfit.MainActivity.ExerciseFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MainActivity.ExerciseFragment.Controller.ExerciseFragmentController;
import com.hoongyan.keepfit.MainActivity.ExerciseFragment.View.ExerciseFragmentView;
import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;
import com.hoongyan.keepfit.MainActivity.FoodFragment.FoodFragment;
import com.hoongyan.keepfit.MainActivity.FoodFragment.View.FoodFragmentView;
import com.hoongyan.keepfit.R;

import static android.app.Activity.RESULT_OK;


public class ExerciseFragment extends Fragment {

    //MVC Components
    private ExerciseFragmentView exerciseFragmentView;
    private ExerciseFragmentController exerciseFragmentController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exerciseFragmentView = new ExerciseFragmentView(ExerciseFragment.this);
        exerciseFragmentController = new ExerciseFragmentController(getActivity().getApplicationContext(), exerciseFragmentView);
        exerciseFragmentView.setMVCController(exerciseFragmentController);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return exerciseFragmentView.inflateFragmentLayout(inflater, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000){
            if(resultCode == RESULT_OK && data != null){

                exerciseFragmentView.onActivityResult(data);
            }
        }
    }
}