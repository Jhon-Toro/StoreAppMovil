package com.cesde.storeapp_android;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cesde.storeapp_android.adapter.CategoryAdapter;
import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar; // Referencia al loader
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_loader);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        fetchCategories();
    }

    private void fetchCategories() {
        // Mostrar el loader antes de hacer el fetch
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        ApiStore apiStore = ApiClient.getClient(this).create(ApiStore.class);
        Call<List<Category>> call = apiStore.getCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                // Ocultar el loader
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryAdapter = new CategoryAdapter(categories, CategoriesActivity.this);
                    recyclerView.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(CategoriesActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Ocultar el loader incluso si falla
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(CategoriesActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
