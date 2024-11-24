package com.cesde.storeapp_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener la URL del Intent
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String path = data.getPath();

            // Manejar la redirección según el path
            if ("/order/success".equals(path)) {
                // Mostrar layout de éxito
                setContentView(R.layout.payment_success);

                // Configurar el botón de "Continuar comprando"
                Button continueButton = findViewById(R.id.btn_continue_shopping);
                continueButton.setOnClickListener(v -> onBackToHomeClicked());
            } else if ("/order/cancel".equals(path)) {
                // Mostrar layout de fallo
                setContentView(R.layout.payment_failed);

                // Configurar el botón de "Intentar nuevamente"
                Button tryAgainButton = findViewById(R.id.btn_try_again);
                tryAgainButton.setOnClickListener(v -> onBackToHomeClicked());
            } else {
                // Si la redirección es desconocida
                setContentView(R.layout.activity_order_result);
                TextView message = findViewById(R.id.message);
                message.setText("Redirección desconocida.");
            }
        } else {
            // Si no hay datos en el Intent
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
