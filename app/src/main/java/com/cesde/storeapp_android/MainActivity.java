package com.cesde.storeapp_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
    private DrawerLayout drawerLayout; // Para manejar el Drawer
    private ActionBarDrawerToggle drawerToggle; // Botón para abrir/cerrar el Drawer
    private RecyclerView recyclerView; // RecyclerView para mostrar productos
    private ProductAdapter adapter; // Adaptador para los productos
    private List<Product> productList = new ArrayList<>(); // Lista de productos
    private NavigationView navigationView; // NavigationView para el menú del Drawer
    private SessionManager sessionManager; // Manejo de la sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar el SessionManager primero
        sessionManager = new SessionManager(this);

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
        updateCartBadge(); // Actualizar el contador del carrito al reingresar a la pantalla principal
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
            Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_products) {
            Toast.makeText(this, "Productos seleccionados", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_categories) {
            Toast.makeText(this, "Categorías seleccionadas", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_my_orders) { // Manejar "Mis Órdenes"
            startActivity(new Intent(this, MyOrdersActivity.class));
        } else if (id == R.id.nav_logout) {
            sessionManager.logout(); // Cierra sesión
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            updateMenuOptions(); // Actualiza el menú después de cerrar sesión
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            // Llama a updateMenuOptions() después de iniciar sesión
            updateMenuOptions();
        } else if (id == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
            updateMenuOptions();
        } else {
            Toast.makeText(this, "Opción no reconocida", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawers(); // Cerrar el Drawer después de seleccionar una opción
        return true;
    }



    private void showProducts() {
        Call<List<Product>> call = ApiClient.getClient(this).create(ApiStore.class).getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    adapter.updateProductList(productList);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
