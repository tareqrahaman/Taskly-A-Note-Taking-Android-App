package com.example.taskly;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TaskModel> arrayList;

    public TaskAdapter(Context context, ArrayList<TaskModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // You MUST create a layout file named 'task_item.xml'
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        final TaskModel currentTask = arrayList.get(position);

        holder.tvTitle.setText(currentTask.getTitle());
        holder.tvDesc.setText(currentTask.getDescription());

        // Set CheckBox status based on model (1=checked, 0=unchecked)
        boolean isCompleted = currentTask.getStatus() == 1;
        holder.checkBox.setChecked(isCompleted);

        // Apply strikethrough effect if completed
        applyStrikeThrough(holder.tvTitle, isCompleted);
        applyStrikeThrough(holder.tvDesc, isCompleted);

        // Ensure the OnCheckedChangeListener is NOT called when the view is recycled/bound
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnClickListener(v -> {
            boolean isChecked = holder.checkBox.isChecked();

            // This ensures the status update logic still runs
            TaskDatabaseHelper taskHelper = new TaskDatabaseHelper(context);
            int newStatus = isChecked ? 1 : 0;

            taskHelper.updateTaskStatus(currentTask.getId(), newStatus);
            currentTask.setStatus(newStatus); // Update model immediately

            // Apply visual change instantly
            applyStrikeThrough(holder.tvTitle, isChecked);
            applyStrikeThrough(holder.tvDesc, isChecked);
        });


        // Click Listener for Editing (Row Click)
        holder.cardView.setOnClickListener(v -> {
            // New Intent to open TaskActivity for editing
            Intent intent = new Intent(context, TaskActivity.class);

            // Pass ALL necessary data for editing
            intent.putExtra("id", currentTask.getId());
            intent.putExtra("title", currentTask.getTitle());
            intent.putExtra("description", currentTask.getDescription());

            context.startActivity(intent);
        });

        // --- Long Click Listener for Deleting Task (Unchanged) ---
        holder.cardView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to permanently delete this task?")
                    .setPositiveButton("DELETE", (dialogInterface, i) -> {
                        TaskDatabaseHelper taskHelper = new TaskDatabaseHelper(context);
                        taskHelper.deleteTask(String.valueOf(currentTask.getId()));

                        // Remove item from list and notify adapter for instant refresh
                        arrayList.remove(position);
                        notifyItemRemoved(position);

                        dialogInterface.dismiss();
                        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setIcon(R.drawable.baseline_delete_24)
                    .show();
            return true;
        });
    }

    // Helper method for strikethrough text styling
    private void applyStrikeThrough(TextView textView, boolean strike) {
        if (strike) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(0.5f); // Fade completed tasks
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;
        CheckBox checkBox;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // You will need to define these IDs in task_item.xml
            tvTitle = itemView.findViewById(R.id.taskTitle);
            tvDesc = itemView.findViewById(R.id.taskDesc);
            checkBox = itemView.findViewById(R.id.taskCheckBox);
            cardView = itemView.findViewById(R.id.taskCardView);
        }
    }
}