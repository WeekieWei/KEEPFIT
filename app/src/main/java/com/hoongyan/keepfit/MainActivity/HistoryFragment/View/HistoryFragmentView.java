package com.hoongyan.keepfit.MainActivity.HistoryFragment.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hoongyan.keepfit.MainActivity.FoodFragment.Controller.FoodFragmentController;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.Controller.HistoryFragmentController;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.HistoryFragment;
import com.hoongyan.keepfit.R;

public class HistoryFragmentView implements HistoryFragmentViewInterface{

    //MVC Component
    private HistoryFragmentController historyFragmentController;

    //Data Field
    private View rootView;
    private FragmentActivity activity;
    private Fragment fragment;

    //View
    private RecyclerView historyRecyclerView;

    public HistoryFragmentView(Fragment fragment){
        this.fragment = fragment;
        activity = fragment.getActivity();
    }

    @Override
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        initializeViews();
        return rootView;
    }

    @Override
    public void setMVCController(HistoryFragmentController mvcController){
        historyFragmentController = mvcController;
    }


    @Override
    public void initializeViews() {
        historyRecyclerView = rootView.findViewById(R.id.historyRecycleView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        historyRecyclerView.setAdapter(null);
    }

    @Override
    public void setRecycleViewAdapter(RecyclerView.Adapter adapter){
        historyRecyclerView.setAdapter(adapter);
    }

    @Override
    public View getAlertDialogLayout(){
        return LayoutInflater.from(fragment.getContext()).inflate(R.layout.layout_input_meal_name, (ViewGroup) fragment.getView(), false);
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
