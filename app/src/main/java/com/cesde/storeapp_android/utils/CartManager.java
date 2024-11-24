package com.cesde.storeapp_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cesde.storeapp_android.model.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String CART_PREFS = "CartPrefs";
    private static final String CART_ITEMS_KEY = "CartItems";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public CartManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public List<CartItem> getCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS_KEY, "");
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, type);
    }

    public void addToCart(CartItem item) {
        List<CartItem> cartItems = getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct_id() == item.getProduct_id()) {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                saveCartItems(cartItems);
                return;
            }
        }
        cartItems.add(item);
        saveCartItems(cartItems);
    }

    public void removeFromCart(int productId) {
        List<CartItem> cartItems = getCartItems();
        cartItems.removeIf(item -> item.getProduct_id() == productId);
        saveCartItems(cartItems);
    }

    public void updateCartItemQuantity(int productId, int newQuantity) {
        List<CartItem> cartItems = getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct_id() == productId) {
                cartItem.setQuantity(newQuantity);
                break;
            }
        }
        saveCartItems(cartItems);
    }

    public void clearCart() {
        saveCartItems(new ArrayList<>());
    }

    public void saveCartItems(List<CartItem> cartItems) {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply();
    }
}
