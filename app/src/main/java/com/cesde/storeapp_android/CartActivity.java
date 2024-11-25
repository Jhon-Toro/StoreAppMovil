package com.cesde.storeapp_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cesde.storeapp_android.adapter.CartAdapter;
import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.CartItem;
import com.cesde.storeapp_android.model.OrderRequest;
import com.cesde.storeapp_android.model.OrderResponse;
import com.cesde.storeapp_android.utils.CartManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdatedListener {

    private RecyclerView recyclerView;
    private CartManager cartManager;
    private TextView totalPriceText;
    private Button checkoutButton;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartManager = new CartManager(this);
        recyclerView = findViewById(R.id.recycler_cart);
        totalPriceText = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartManager.getCartItems(), cartManager, this);
        recyclerView.setAdapter(adapter);

        calculateTotalPrice();

        checkoutButton.setOnClickListener(v -> createOrder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    @Override
    public void onCartUpdated() {
        cartManager.saveCartItems(cartManager.getCartItems()); // Guardar el estado actual
        calculateTotalPrice(); // Calcular el precio total
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("update_cart", true); // Pasar un indicador para actualizar el carrito
        setResult(RESULT_OK, intent);
    }

    private void calculateTotalPrice() {
        List<CartItem> cartItems = cartManager.getCartItems();
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.format("Total: $%.2f", total));
    }

    private void createOrder() {
        List<CartItem> cartItems = cartManager.getCartItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir la solicitud de orden
        OrderRequest orderRequest = new OrderRequest(cartItems);
        ApiStore apiService = ApiClient.getClient(this).create(ApiStore.class);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Call<OrderResponse> call = apiService.createOrder("Bearer " + token, orderRequest);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String approvalUrl = response.body().getApproval_url();
                    openPayPal(approvalUrl);
                } else {
                    Toast.makeText(CartActivity.this, "Error al crear la orden", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPayPal(String approvalUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(approvalUrl));
        startActivity(browserIntent);
    }
}
