package com.cesde.storeapp_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.AuthResponse;
import com.cesde.storeapp_android.model.LoginRequest;
import com.cesde.storeapp_android.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private SharedPreferences sharedPreferences;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // Inicializar SharedPreferences para guardar el token
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Configurar alternancia de visibilidad para la contraseña
        togglePasswordVisibility();

        // Configurar acción del botón de login
        btnLogin.setOnClickListener(v -> loginUser());

        // Configurar redirección al RegisterActivity
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        ApiStore apiService = ApiClient.getClient(this).create(ApiStore.class);
        Call<AuthResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    String username = response.body().getUser().getUsername();

                    // Siempre sobrescribe la sesión anterior
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    sessionManager.saveUserSession(token, username);

                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("Login", "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }


//    private void saveUserSession(String token, boolean isAdmin) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("token", token);
//        editor.putBoolean("isAdmin", isAdmin);
//        editor.putBoolean("isLoggedIn", true);
//        editor.apply();
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void togglePasswordVisibility() {
        etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[2].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;
                    etPassword.setInputType(isPasswordVisible
                            ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            isPasswordVisible ? R.drawable.ic_visibility_off : R.drawable.ic_visibility, 0);
                    return true;
                }
            }
            return false;
        });
    }
}
