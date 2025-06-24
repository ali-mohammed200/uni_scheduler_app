package com.example.mytestapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestapplication.adapters.TermAdapter;
import com.example.mytestapplication.database.TermDAO;
import com.example.mytestapplication.models.Term;

import java.util.List;

public class TermsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TermAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        recyclerView = findViewById(R.id.recyclerViewTerms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TermDAO dao = new TermDAO(this);
        List<Term> terms = dao.getAllTerms();
        adapter = new TermAdapter(terms);
        recyclerView.setAdapter(adapter);
    }
}
