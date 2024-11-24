package com.cesde.storeapp_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cesde.storeapp_android.adapter.OrderAdapter; // Un adaptador para mostrar órdenes
import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchUserOrders();
    }

    private void fetchUserOrders() {
        // Obtener el token del SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Inicia sesión para ver tus órdenes", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiStore apiStore = ApiClient.getClient().create(ApiStore.class);
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
