package com.cesde.storeapp_android.api;

import android.content.Context;

import com.cesde.storeapp_android.utils.SessionManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log completo para depuración

        // Interceptor para añadir el token de autorización
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder();

                // Obtener el token desde SessionManager
                SessionManager sessionManager = new SessionManager(context);
                String token = sessionManager.getAccessToken();

                if (token != null) {
                    // Agregar el encabezado Authorization si el token existe
                    requestBuilder.header("Authorization", "Bearer " + token);
                }

                Request modifiedRequest = requestBuilder.build();
                return chain.proceed(modifiedRequest);
            }
        };

        // Construir el cliente OkHttp con los interceptores
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging) // Logging
                .addInterceptor(authInterceptor) // Token Authorization
                .build();

        // Crear la instancia de Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://storeapi-production-ac79.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
