package com.cesde.storeapp_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cesde.storeapp_android.R;
import com.cesde.storeapp_android.model.CartItem;
import com.cesde.storeapp_android.utils.CartManager;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartItem> cartItems;
    private final CartManager cartManager;
    private final OnCartUpdatedListener onCartUpdatedListener;

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    public CartAdapter(List<CartItem> cartItems, CartManager cartManager, OnCartUpdatedListener onCartUpdatedListener) {
        this.cartItems = cartItems;
        this.cartManager = cartManager;
        this.onCartUpdatedListener = onCartUpdatedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.productName.setText(item.getTitle());
        holder.productPrice.setText(String.format("$%.2f", item.getPrice() * item.getQuantity()));
        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        // Cargar la imagen del producto
        Glide.with(holder.productImage.getContext())
                .load(item.getImageUrl())
                .into(holder.productImage);

        // Incrementar cantidad
        holder.increaseButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            cartManager.updateCartItemQuantity(item.getProduct_id(), item.getQuantity());
            notifyItemChanged(position);
            onCartUpdatedListener.onCartUpdated();
        });

        // Decrementar cantidad
        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartManager.updateCartItemQuantity(item.getProduct_id(), item.getQuantity());
                notifyItemChanged(position);
                onCartUpdatedListener.onCartUpdated();
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            cartManager.removeFromCart(item.getProduct_id()); // Remover del almacenamiento
            cartItems.remove(position); // Remover de la lista actual
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            onCartUpdatedListener.onCartUpdated(); // Notificar la actualización
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        TextView productName, productPrice, productQuantity;
        ImageButton removeButton, increaseButton, decreaseButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_title);
            productPrice = itemView.findViewById(R.id.tv_price);
            productImage = itemView.findViewById(R.id.iv_product_image);
            productQuantity = itemView.findViewById(R.id.tv_quantity);
            increaseButton = itemView.findViewById(R.id.btn_increase);
            decreaseButton = itemView.findViewById(R.id.btn_decrease);
            removeButton = itemView.findViewById(R.id.btn_remove);
        }
    }
}
