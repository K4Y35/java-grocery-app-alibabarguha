package com.example.alibabarguha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */public class ProductFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    private DatabaseReference productReference;
    private DatabaseReference categoryReference;
    private TextView productpage;

    private static final String ARG_CATEGORY_ID = "categoryId";

    public static ProductFragment newInstance(String categoryId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        productRecyclerView.setLayoutManager(layoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        productRecyclerView.setAdapter(productAdapter);


        productReference = FirebaseDatabase.getInstance().getReference("Products");
        categoryReference = FirebaseDatabase.getInstance().getReference("Categories");


        productpage = view.findViewById(R.id.productpage);


        if (getArguments() != null) {
            String categoryId = getArguments().getString(ARG_CATEGORY_ID);

            if (categoryId != null) {
                loadProductsByCategory(categoryId);
                loadCategoryName(categoryId);
            }
        }




        return view;
    }

    private void loadCategoryName(String categoryId) {
        categoryReference.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String categoryName = snapshot.child("name").getValue(String.class);
                    if (categoryName != null) {
                        productpage.setText(categoryName);
                        Log.d("fix", "category name: " +categoryName);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadProductsByCategory(String categoryId) {
        productReference.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
