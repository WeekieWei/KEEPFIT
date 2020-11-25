package com.hoongyan.keepfit.MainActivity.HistoryFragment.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hoongyan.keepfit.MVCView;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.Controller.HistoryFragmentController;
import com.hoongyan.keepfit.R;

public interface HistoryFragmentViewInterface extends MVCView {

    public void setRecycleViewAdapter(RecyclerView.Adapter adapter);

    public View getAlertDialogLayout();

    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container);

    public void setMVCController(HistoryFragmentController mvcController);
}
