package com.example.alibabarguha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alibabarguha.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Product> mProductList;

    public ProductAdapter(Context context, List<Product> productList) {
        mContext = context;
        mProductList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = mProductList.get(position);
        holder.productName.setText(currentProduct.getProductName());
        holder.productQuantity.setText(currentProduct.getProductQuantity());
        holder.productPrice.setText(String.valueOf(currentProduct.getProductPrice()));
        Glide.with(mContext).load(currentProduct.getProductImageUrl()).into(holder.productImage);

        holder.addToCartButton.setOnClickListener(v -> addToCart(currentProduct));
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    private void addToCart(Product product) {
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("Cart");
        String productId = cartReference.push().getKey();
        if (productId != null) {
            cartReference.child(productId).setValue(product);
        }

        Toast.makeText(mContext, "Item Added", Toast.LENGTH_SHORT).show();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productQuantity;
        public ImageView productImage;
        public TextView productPrice;
        public Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartButton = itemView.findViewById(R.id.addtoCartBtn);
        }
    }
}
