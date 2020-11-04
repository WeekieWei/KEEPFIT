package com.hoongyan.keepfit.MainActivity.HistoryFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;
import com.hoongyan.keepfit.MainActivity.FoodFragment.FoodFragment;
import com.hoongyan.keepfit.MainActivity.FoodFragment.View.FoodFragmentView;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.Controller.HistoryFragmentController;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.View.HistoryFragmentView;
import com.hoongyan.keepfit.R;

public class HistoryFragment extends Fragment {

    //MVC Components
    private HistoryFragmentView historyFragmentView;
    private HistoryFragmentController historyFragmentController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyFragmentView = new HistoryFragmentView(HistoryFragment.this);
        historyFragmentController = new HistoryFragmentController(this.getContext(), historyFragmentView);
        historyFragmentView.setMVCController(historyFragmentController);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return historyFragmentView.inflateFragmentLayout(inflater, container);
    }
}