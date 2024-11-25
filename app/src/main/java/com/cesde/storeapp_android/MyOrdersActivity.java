package com.cesde.storeapp_android;

import android.os.Bundle;
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
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchUserOrders();
    }

    private void fetchUserOrders() {
        // Validar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Por favor, inicia sesión para ver tus órdenes", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no está logueado
            return;
        }

        String token = sessionManager.getAccessToken();

        ApiStore apiStore = ApiClient.getClient(this).create(ApiStore.class);
        Call<List<Order>> call = apiStore.getUserOrders("Bearer " + token);

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    adapter = new OrderAdapter(orders);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MyOrdersActivity.this, "Error al cargar órdenes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(MyOrdersActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
