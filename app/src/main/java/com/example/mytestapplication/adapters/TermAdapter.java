package com.example.mytestapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.R;
import com.example.mytestapplication.models.Term;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<Term> terms;

    public TermAdapter(List<Term> terms) {
        this.terms = terms;
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

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_term, parent, false);
        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Term term = terms.get(position);
        holder.termTitle.setText(term.getTitle());
        holder.termDates.setText(term.getStartDate() + " – " + term.getEndDate());
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }
}
