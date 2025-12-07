package com.example.taskly;
// Make sure this matches your package name

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NoteModel> arrayList;

    public NoteAdapter(Context context, ArrayList<NoteModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // You'll need to create a layout file named 'note_item.xml' for how each note looks
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        final NoteModel currentNote = arrayList.get(position);

        // Set the data from the NoteModel to the UI elements
        holder.tvTitle.setText(currentNote.getTitle());
        holder.tvDesc.setText(currentNote.getDescription());

        holder.cardView.setOnClickListener(v -> {
            // 1. Create Intent pointing to the new Activity
            Intent intent = new Intent(context, NoteUpdate.class);

            // 2. Pass the existing data
            intent.putExtra("id", currentNote.getId());
            intent.putExtra("title", currentNote.getTitle());
            intent.putExtra("description", currentNote.getDescription());

            // 3. Start the Activity
            context.startActivity(intent);
        });

        // --- Long Click Listener for Deleting Note ---
        holder.cardView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to permanently delete this note?")
                    .setPositiveButton("DELETE", (dialogInterface, i) -> {

                        // *** Correctly calls the new Database Helper ***
                        NoteDatabaseHelper noteHelper = new NoteDatabaseHelper(context);
                        String id = String.valueOf(currentNote.getId());

                        noteHelper.deleteData(id);

                        // To refresh the list immediately without restarting the whole MainActivity:
                        arrayList.remove(position);
                        notifyItemRemoved(position);

                        dialogInterface.dismiss();
                        Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();

                        // Optional: If you rely on restarting MainActivity for a full refresh:
                        // context.startActivity(new Intent(context, MainActivity.class));
                    })
                    .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                    // Replace baseline_add_alert_24 with a trash can or relevant icon if you have one
                    .setIcon(R.drawable.baseline_delete_24)
                    .show();
            return true; // Return true to consume the long click event
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs need to be defined in note_item.xml
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}