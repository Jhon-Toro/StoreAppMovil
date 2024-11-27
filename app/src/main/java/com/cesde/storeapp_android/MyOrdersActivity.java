package com.cesde.storeapp_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cesde.storeapp_android.adapter.OrderAdapter;
import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.Order;
import com.cesde.storeapp_android.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private TextView tvEmptyOrders;
    private Button btnGoToShop;
    private ProgressBar progressBar; // Loader
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        sessionManager = new SessionManager(this);

        // Inicializar vistas
        recyclerView = findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvEmptyOrders = findViewById(R.id.tv_empty_orders);
        btnGoToShop = findViewById(R.id.btn_go_to_shop);
        progressBar = findViewById(R.id.progress_loader);

        btnGoToShop.setOnClickListener(v -> finish()); // Botón para cerrar esta actividad

        fetchUserOrders();
    }

    private void fetchUserOrders() {
        // Validar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Por favor, inicia sesión para ver tus órdenes", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no está logueado
            return;
        }

        // Mostrar el loader
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyOrders.setVisibility(View.GONE);

        String token = sessionManager.getAccessToken();

        ApiStore apiStore = ApiClient.getClient(this).create(ApiStore.class);
        Call<List<Order>> call = apiStore.getUserOrders("Bearer " + token);

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                // Ocultar el loader después de la carga
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();

                    if (orders.isEmpty()) {
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        btnGoToShop.setVisibility(View.VISIBLE);
                    } else {
                        adapter = new OrderAdapter(orders);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                    btnGoToShop.setVisibility(View.VISIBLE);
                    Toast.makeText(MyOrdersActivity.this, "Error al cargar órdenes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                // Ocultar el loader incluso si falla
                progressBar.setVisibility(View.GONE);
                tvEmptyOrders.setVisibility(View.VISIBLE);
                btnGoToShop.setVisibility(View.VISIBLE);

                Toast.makeText(MyOrdersActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
