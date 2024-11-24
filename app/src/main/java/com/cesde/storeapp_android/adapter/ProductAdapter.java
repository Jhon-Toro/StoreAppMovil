package com.cesde.storeapp_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cesde.storeapp_android.MainActivity;
import com.cesde.storeapp_android.R;
import com.cesde.storeapp_android.model.CartItem;
import com.cesde.storeapp_android.model.Product;
import com.cesde.storeapp_android.utils.CartManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private CartManager cartManager;
    private Runnable onCartUpdated;

    public ProductAdapter(List<Product> productList, Context context, CartManager cartManager, Runnable onCartUpdated) {
        this.productList = productList != null ? productList : new ArrayList<>();
        this.context = context;
        this.cartManager = cartManager;
        this.onCartUpdated = onCartUpdated;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        private TextView productName, productDescription, productPrice;
        private TextView addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.iv_portada);
            productName = itemView.findViewById(R.id.product_title);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.btn_add_to_cart);
        }

        public void bind(Product product, CartManager cartManager, Runnable onCartUpdated) {
            productName.setText(product.getTitle());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            Glide.with(productImage.getContext()).load(product.getImages().get(0)).into(productImage);

            addToCartButton.setOnClickListener(v -> {
                CartItem cartItem = new CartItem();
                cartItem.setProduct_id(product.getId()); // Aquí configuramos el product_id
                cartItem.setTitle(product.getTitle());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(1);

                cartManager.addToCart(cartItem);
                Toast.makeText(productImage.getContext(), product.getTitle() + " añadido al carrito", Toast.LENGTH_SHORT).show();
                onCartUpdated.run();
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getTitle());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));

        // Cargar la imagen del producto
        Glide.with(holder.productImage.getContext())
                .load(product.getImages().get(0))
                .into(holder.productImage);

        // Agregar al carrito
        holder.addToCartButton.setOnClickListener(v -> {
            CartItem cartItem = new CartItem();
            cartItem.setProduct_id(product.getId());
            cartItem.setTitle(product.getTitle());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(1);
            cartItem.setImageUrl(product.getImages().get(0)); // Copiar la imagen del producto

            cartManager.addToCart(cartItem);
            Toast.makeText(holder.productImage.getContext(), product.getTitle() + " añadido al carrito", Toast.LENGTH_SHORT).show();
            onCartUpdated.run();
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> newProducts) {
        productList.clear();
        if (newProducts != null) {
            productList.addAll(newProducts);
        }
        notifyDataSetChanged();
    }
}
