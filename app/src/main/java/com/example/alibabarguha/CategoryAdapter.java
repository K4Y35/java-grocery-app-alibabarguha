package com.example.alibabarguha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alibabarguha.R;

import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<Category> mCategoryList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(String categoryId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        mContext = context;
        mCategoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_category_item, parent, false);
        return new CategoryViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category currentCategory = mCategoryList.get(position);
        holder.categoryName.setText(currentCategory.getName());
        Glide.with(mContext).load(currentCategory.getImageUrl()).into(holder.categoryImage);
        holder.itemView.setTag(currentCategory.getCategoryId()); // Set the category ID as tag
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView categoryImage;

        public CategoryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryImage = itemView.findViewById(R.id.categoryImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick((String) v.getTag());
                    }
                }
            });
        }
    }
}
