package com.cesde.storeapp_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cesde.storeapp_android.utils.CartManager;

public class OrderResultActivity extends AppCompatActivity {

    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartManager = new CartManager(this);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String path = data.getPath();

            if ("/order/success".equals(path)) {
                cartManager.clearCart(); // Limpia el carrito

                setContentView(R.layout.payment_success);
                Button continueButton = findViewById(R.id.btn_continue_shopping);
                continueButton.setOnClickListener(v -> onBackToHomeClicked());
            } else if ("/order/cancel".equals(path)) {
                setContentView(R.layout.payment_failed);
                Button tryAgainButton = findViewById(R.id.btn_try_again);
                tryAgainButton.setOnClickListener(v -> onBackToHomeClicked());
            } else {
                setContentView(R.layout.activity_order_result);
                TextView message = findViewById(R.id.message);
                message.setText("Redirección desconocida.");
            }
        } else {
            setContentView(R.layout.activity_order_result);
            TextView message = findViewById(R.id.message);
            message.setText("No se pudo determinar el estado del pago.");
        }
    }


    private void onBackToHomeClicked() {
        // Redirigir al inicio o pantalla principal
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
