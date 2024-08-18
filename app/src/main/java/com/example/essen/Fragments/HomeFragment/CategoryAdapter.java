package com.example.essen.Fragments.HomeFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essen.R;
import com.example.essen.pojo.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private ArrayList<Category> categoriesList = new ArrayList<>();
    private OnItemClickListener onItemClicked;

    public void setOnItemClicked(OnItemClickListener onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    public void setCategoriesList(List<Category> categoriesList) {
        this.categoriesList = new ArrayList<>(categoriesList);
        this.categoriesList.addAll(categoriesList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoriesList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(category.getStrCategoryThumb())
                //.placeholder(R.drawable.berger)
                //.error(R.drawable.berger)
                .into(holder.imageC);

        holder.tvC.setText(category.getStrCategory());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClicked != null) {
                onItemClicked.onItemClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageC;
        TextView tvC;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageC = itemView.findViewById(R.id.imageC);
            tvC = itemView.findViewById(R.id.tvC);
        }
    }
}
