package com.cesde.storeapp_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cesde.storeapp_android.MainActivity;
import com.cesde.storeapp_android.R;
import com.cesde.storeapp_android.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private Context context;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        if (categoryList != null) {
            this.categoryList = categoryList;
        }
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        // Cargar la imagen usando Glide
        Glide.with(holder.categoryImage.getContext())
                .load(category.getImageUrl())
                .into(holder.categoryImage);

        // Click listener para redirigir a MainActivity con el CATEGORY_ID
        holder.itemView.setOnClickListener(v -> {
            if (context != null) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
