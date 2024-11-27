package com.cesde.storeapp_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cesde.storeapp_android.adapter.ProductAdapter;
import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.Product;
import com.cesde.storeapp_android.utils.CartManager;
import com.cesde.storeapp_android.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private CartManager cartManager;
    private TextView cartBadge;
    private ImageView cartIcon;
    private ProgressBar progressBar; // Loader
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private NavigationView navigationView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar el SessionManager primero
        sessionManager = new SessionManager(this);

        // Inicializar ProgressBar
        progressBar = findViewById(R.id.progress_loader);

        // Configuración de NavigationView
        navigationView = findViewById(R.id.navigation_view);
        if (navigationView.getHeaderCount() == 0) {
            navigationView.inflateHeaderView(R.layout.nav_header);
        }
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Actualizar opciones del menú después de inicializar sessionManager
        updateMenuOptions();

        cartManager = new CartManager(this);
        cartBadge = findViewById(R.id.cart_badge);
        cartIcon = findViewById(R.id.cart_icon);

        cartIcon.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.get_products);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(productList, this, cartManager, this::updateCartBadge);
        recyclerView.setAdapter(adapter);

        // Configuración del DrawerLayout
        drawerLayout = findViewById(R.id.main);
        setupToolbarAndDrawer();

        // Actualizar contador inicial del carrito
        updateCartBadge();

        // Cargar productos desde la API
        showProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCartBadge();

        if (cartManager.getCartItems().isEmpty()) {
            adapter.updateProductList(new ArrayList<>()); // Si es necesario actualizar los productos
        }
    }


    private void updateCartBadge() {
        int cartItemCount = cartManager.getCartItems().size();
        if (cartItemCount > 0) {
            cartBadge.setText(String.valueOf(cartItemCount));
            cartBadge.setVisibility(View.VISIBLE);
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configuración del botón del Drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void updateMenuOptions() {
        Menu menu = navigationView.getMenu();
        boolean isLoggedIn = sessionManager.isLoggedIn();

        // Mostrar/Ocultar opciones basadas en el estado del usuario
        menu.findItem(R.id.nav_login).setVisible(!isLoggedIn);
        menu.findItem(R.id.nav_register).setVisible(!isLoggedIn);
        menu.findItem(R.id.nav_logout).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_my_orders).setVisible(isLoggedIn);

        // Actualizar encabezado dinámicamente
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = headerView.findViewById(R.id.tv_username);

        if (isLoggedIn) {
            String username = sessionManager.getUsername();
            tvUsername.setText("Hola, " + (username.isEmpty() ? "Usuario" : username));
            tvUsername.setVisibility(View.VISIBLE);
        } else {
            tvUsername.setText("");
            tvUsername.setVisibility(View.GONE);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_products) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(this, CategoriesActivity.class));
            finish();
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_my_orders) {
            startActivity(new Intent(this, MyOrdersActivity.class));
        } else if (id == R.id.nav_logout) {
            sessionManager.logout();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            updateMenuOptions();
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Opción no reconocida", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void showProducts() {
        // Mostrar el loader antes de cargar los productos
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        ApiStore apiStore = ApiClient.getClient(this).create(ApiStore.class);
        Call<List<Product>> call = apiStore.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                // Ocultar el loader después de la carga
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    adapter.updateProductList(productList);
                } else {
                    Toast.makeText(MainActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Ocultar el loader incluso si falla
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
