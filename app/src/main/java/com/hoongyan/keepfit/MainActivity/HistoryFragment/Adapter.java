package com.hoongyan.keepfit.MainActivity.HistoryFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hoongyan.keepfit.JavaClass.HistoryObject;
import com.hoongyan.keepfit.MVCModel;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.Controller.HistoryFragmentController;
import com.hoongyan.keepfit.R;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.HistoryViewHolder>{

    private ArrayList<HistoryObject> historyList;
    private Intent intent;
    private Context context;
    private HistoryFragmentController historyFragmentController;

    public static class HistoryViewHolder extends RecyclerView.ViewHolder{

        private TextView title, description, calValue;
        private ImageButton editButton, deleteButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.historyTitle);
            description = itemView.findViewById(R.id.historyDescription);
            calValue = itemView.findViewById(R.id.calValue);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public Adapter(ArrayList<HistoryObject> historyList, HistoryFragmentController historyFragmentController, Context context){
        this.historyList = historyList;
        this.historyFragmentController = historyFragmentController;
        this.context = context;

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        final HistoryObject currentItem = historyList.get(position);

        String title = currentItem.getHistoryTitle();
        if(title.length() > 18) title = title.substring(0, 17) + "..";

        String date;
        Instant itemInstant = currentItem.getTimestamp().toDate().toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant nowInstant = Instant.now().truncatedTo(ChronoUnit.DAYS);

        if(nowInstant.compareTo(itemInstant) == 0){
            date = "Today";
        }else if(nowInstant.minus(1, ChronoUnit.DAYS).compareTo(itemInstant) == 0){
            date = "Yesterday";
        }else{
            date = currentItem.getTimestamp().toDate().toString();
        }

        holder.title.setText(title);
        holder.description.setText(date);
        holder.calValue.setText(String.format("%.2f", currentItem.getTotalCalorie()) + " cal");
        switch (currentItem.getType()){
            case 1 : holder.calValue.setTextColor(Color.parseColor("#00a82d"));
            break;
            case 2 : holder.calValue.setTextColor(Color.parseColor("#FFFF0000"));
            break;
        }

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyFragmentController.editItemAtPosition(position, currentItem.getDocumentID());
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Long click required to delete", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                historyFragmentController.removeItemAtPosition(position, currentItem.getDocumentID(), currentItem.getTimestamp().toDate(), currentItem.getFoodSlotID(), currentItem.getTotalCalorie(), currentItem.getType());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
