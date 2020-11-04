package com.hoongyan.keepfit.MainActivity.HistoryFragment.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hoongyan.keepfit.JavaClass.HistoryObject;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.Adapter;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.View.HistoryFragmentView;
import com.hoongyan.keepfit.R;

import java.util.ArrayList;

public class HistoryFragmentController implements HistoryFragmentControllerInterface {

    //MVC Components
    private HistoryFragmentView historyFragmentView;
    private MVCModel mvcModel;

    //Data Field
    private Context context;
    private ArrayList<String> documentIDList;
    private ArrayList<HistoryObject> historyList;
    private RecyclerView.Adapter adapter;

    public HistoryFragmentController(Context context, HistoryFragmentView historyFragmentView){
        this.context = context;
        this.historyFragmentView = historyFragmentView;
        mvcModel = new MVCModel(context, true);
        mvcModel.getHistoryList(new MVCModel.HistoryObjectCallback() {
            @Override
            public void onDataFetched(ArrayList<HistoryObject> historyList) {
                HistoryFragmentController.this.historyList = historyList;
                HistoryFragmentController.this.adapter = new Adapter(historyList, HistoryFragmentController.this, context);
                historyFragmentView.setRecycleViewAdapter(adapter);
            }
        });
    }

    public void removeItemAtPosition(int position, String documentID){
        mvcModel.removeHistoryItem(new MVCModel.TaskResultStatus() {
            @Override
            public void onResultReturn(boolean result) {
                if(result){
                    historyList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, historyList.size());
                }else
                    Toast.makeText(historyFragmentView.getRootView().getContext(), "Could not delete without internet", Toast.LENGTH_SHORT).show();
            }
        }, documentID);
    }

    public void editItemAtPosition(int position, String documentID){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Meal Name");
        View viewInflated = historyFragmentView.getAlertDialogLayout();
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setText(historyList.get(position).getHistoryTitle());
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String text = input.getText().toString();

                mvcModel.updateTitle(new MVCModel.TaskResultStatus() {
                    @Override
                    public void onResultReturn(boolean result) {
                        if(result){
                            historyList.get(position).setHistoryTitle(text);
                            adapter.notifyItemChanged(position);
                        }else
                            Toast.makeText(historyFragmentView.getRootView().getContext(), "Could not edit without internet", Toast.LENGTH_SHORT).show();
                    }
                }, documentID, text);
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
}
