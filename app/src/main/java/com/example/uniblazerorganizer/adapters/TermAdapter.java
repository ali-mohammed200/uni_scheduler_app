package com.example.uniblazerorganizer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uniblazerorganizer.AddTermActivity;
import com.example.uniblazerorganizer.R;
import com.example.uniblazerorganizer.database.CourseDAO;
import com.example.uniblazerorganizer.database.TermDAO;
import com.example.uniblazerorganizer.models.Term;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<Term> terms;

    public TermAdapter(List<Term> terms) {
        this.terms = terms;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_term, parent, false);
        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Term term = terms.get(position);
        holder.termTitle.setText(term.getTitle());
        holder.termDates.setText(term.getStartDate() + " – " + term.getEndDate());
        holder.itemView.setTag(term);

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, term);
            return true;
        });
    }

    private void showPopupMenu(View anchorView, Term term) {
        PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_card_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                openEditForm(anchorView.getContext(), term);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                confirmAndDelete(anchorView.getContext(), term);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void confirmAndDelete(Context context, Term term) {
        new AlertDialog.Builder(context).setTitle("Delete Term").setMessage("Are you sure you want to delete this term? Terms with courses will not be deleted").setPositiveButton("Delete", (dialog, which) -> {
            // Delete from DB
            TermDAO dao = new TermDAO(context);
            CourseDAO c_dao = new CourseDAO(context);

            if (c_dao.countCoursesByTermId(term.getId()) == 0) {
                dao.deleteTerm(term.getId());
                // Update adapter
                terms.remove(term);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Delete associated courses for this term first", Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton("Cancel", null).show();
    }

    private void openEditForm(Context context, Term term) {
        Intent intent = new Intent(context, AddTermActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("term", term);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public void setTerms(List<Term> newTerms) {
        this.terms = newTerms;
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView termTitle;
        TextView termDates;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            termTitle = itemView.findViewById(R.id.termTitle);
            termDates = itemView.findViewById(R.id.termDates);
        }
    }
}
