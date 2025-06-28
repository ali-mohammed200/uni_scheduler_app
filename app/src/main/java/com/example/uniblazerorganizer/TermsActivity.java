package com.example.uniblazerorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uniblazerorganizer.adapters.TermAdapter;
import com.example.uniblazerorganizer.database.TermDAO;
import com.example.uniblazerorganizer.models.Term;

import java.util.List;

public class TermsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TermAdapter adapter;
    private ActivityResultLauncher<Intent> addTermLauncher;
    private TermDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show Up button
        getSupportActionBar().setTitle("Terms");

        recyclerView = findViewById(R.id.recyclerViewTerms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTerms();

        addTermLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Reload updated data
                List<Term> updatedTerms = dao.getAllTerms();
                adapter.setTerms(updatedTerms);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void addTerm(View view) {
        Intent intent = new Intent(TermsActivity.this, AddTermActivity.class);
        addTermLauncher.launch(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadTerms();
    }


    private void loadTerms() {
        dao = new TermDAO(this);
        List<Term> terms = dao.getAllTerms();
        adapter = new TermAdapter(terms);
        recyclerView.setAdapter(adapter);
    }

    public void showDetailTerm(View view) {
        Term clickedTerm = (Term) view.getTag(); // Retrieve the term from the adapter
        Intent intent = new Intent(this, DetailTermActivity.class);
        intent.putExtra("term", clickedTerm);
        startActivity(intent);
    }

    //    TODO: Remove this Up logic into a base class that inherits this behavior
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
