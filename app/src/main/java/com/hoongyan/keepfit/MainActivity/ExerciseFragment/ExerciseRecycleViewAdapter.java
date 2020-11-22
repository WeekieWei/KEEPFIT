package com.hoongyan.keepfit.MainActivity.ExerciseFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hoongyan.keepfit.JavaClass.ExerciseObject;
import com.hoongyan.keepfit.MainActivity.HistoryFragment.Adapter;
import com.hoongyan.keepfit.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExerciseRecycleViewAdapter extends RecyclerView.Adapter<ExerciseRecycleViewAdapter.ExerciseViewHolder> {

    private ArrayList<ExerciseObject> exerciseList;
    private float weight;

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder{

        private TextView exerciseName, exerciseMET, calValue;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseMET = itemView.findViewById(R.id.exerciseMET);
            calValue = itemView.findViewById(R.id.calValue);
        }
    }

    public ExerciseRecycleViewAdapter(ArrayList<ExerciseObject> exerciseList, float weight){
        this.exerciseList = exerciseList;
        this.weight = weight;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ExerciseRecycleViewAdapter.ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        final ExerciseObject currentItem = exerciseList.get(position);

        double met = currentItem.getExerciseMET();
        holder.exerciseName.setText(currentItem.getExerciseName());
        holder.exerciseMET.setText("MET: " + String.format("%.1f", met));

        double calVal = met * 3.5 * weight / 200;

        holder.calValue.setText(String.format("%.2f", calVal) + " cal/min");

    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }





}
