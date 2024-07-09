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

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context mContext;
    private List<Product> mCartItemList;

    public CartAdapter(Context context, List<Product> cartItemList) {
        mContext = context;
        mCartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product currentProduct = mCartItemList.get(position);
        holder.cart_name.setText(currentProduct.getProductName());
        holder.cart_quantity.setText(currentProduct.getProductQuantity());
        holder.cart_price.setText(String.valueOf(currentProduct.getProductPrice()));
        Glide.with(mContext).load(currentProduct.getProductImageUrl()).into(holder.cart_image);
    }

    @Override
    public int getItemCount() {
        return mCartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView cart_name;
        public TextView cart_quantity;
        public ImageView cart_image;
        public TextView cart_price;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_name = itemView.findViewById(R.id.cart_name);
            cart_price = itemView.findViewById(R.id.cart_price);
            cart_quantity = itemView.findViewById(R.id.cart_quantity);
            cart_image = itemView.findViewById(R.id.cart_image);
        }
    }
}
