package com.cesde.storeapp_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cesde.storeapp_android.adapter.ReviewAdapter;
import com.cesde.storeapp_android.api.ApiClient;
import com.cesde.storeapp_android.api.ApiStore;
import com.cesde.storeapp_android.model.Product;
import com.cesde.storeapp_android.model.Review;
import com.cesde.storeapp_android.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductTitle, tvProductPrice;
    private RecyclerView rvReviews;
    private RatingBar rbRating;
    private EditText etReviewComment;
    private Button btnSubmitReview, btnAddToCart;

    private int productId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Inicializar vistas
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductTitle = findViewById(R.id.tv_product_title);
        tvProductPrice = findViewById(R.id.tv_product_price);
        rvReviews = findViewById(R.id.rv_reviews);
        rbRating = findViewById(R.id.rb_rating);
        etReviewComment = findViewById(R.id.et_review_comment);
        btnSubmitReview = findViewById(R.id.btn_submit_review);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        sessionManager = new SessionManager(this);

        // Obtener el ID del producto
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        if (productId != -1) {
            fetchProductDetails(productId);
        }

        // Enviar reseña
        btnSubmitReview.setOnClickListener(v -> submitReview());

        // Agregar al carrito
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchProductDetails(int productId) {
        ApiStore apiStore = ApiClient.getClient(this).create(ApiStore.class);
        Call<Product> call = apiStore.getProductById(productId);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();

                    // Configurar detalles del producto
                    tvProductTitle.setText(product.getTitle());
                    tvProductPrice.setText("Precio: $" + product.getPrice());
                    Glide.with(ProductDetailActivity.this).load(product.getImages().get(0)).into(ivProductImage);

                    if (product.getReviews() != null && !product.getReviews().isEmpty()) {
                        ReviewAdapter reviewAdapter = new ReviewAdapter(product.getReviews());
                        rvReviews.setAdapter(reviewAdapter);
                        rvReviews.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this));
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "No hay reseñas disponibles", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Error al cargar detalles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void submitReview() {
        String comment = etReviewComment.getText().toString().trim();
        float rating = rbRating.getRating();

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("comment", comment);
        reviewData.put("product_id", productId);
        reviewData.put("rating", rating);

        ApiStore apiStore = ApiClient.getClient(this).create(ApiStore.class);
        Call<Review> call = apiStore.submitReview("Bearer " + sessionManager.getAccessToken(), reviewData);

        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, "Reseña enviada", Toast.LENGTH_SHORT).show();
                    fetchProductDetails(productId);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Error al enviar reseña", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
