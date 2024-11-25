package com.cesde.storeapp_android.api;

import com.cesde.storeapp_android.model.AuthResponse;
import com.cesde.storeapp_android.model.LoginRequest;
import com.cesde.storeapp_android.model.Order;
import com.cesde.storeapp_android.model.OrderRequest;
import com.cesde.storeapp_android.model.OrderResponse;
import com.cesde.storeapp_android.model.Product;
import com.cesde.storeapp_android.model.RegisterRequest;
import com.cesde.storeapp_android.model.RegisterResponse;
import com.cesde.storeapp_android.model.Review;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiStore {

    @GET("products/")
    Call<List<Product>> getProducts();

    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("/orders/")
    Call<OrderResponse> createOrder(
            @Header("Authorization") String token,
            @Body OrderRequest orderRequest
    );

    @GET("/orders/my_orders")
    Call<List<Order>> getUserOrders(@Header("Authorization") String token);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    @POST("reviews/")
    Call<Review> submitReview(@Header("Authorization") String token, @Body Map<String, Object> reviewData);

}
