package com.example.alibabarguha;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView categoryRecyclerView;
    private RecyclerView productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Category> categoryList;
    private List<Product> productList;

    private DatabaseReference categoryReference;
    private DatabaseReference productReference;

    private String email;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);


        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        productRecyclerView = view.findViewById(R.id.popularproductRecyclerView);

        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        productAdapter = new ProductAdapter(getContext(), productList);

        categoryRecyclerView.setAdapter(categoryAdapter);
        productRecyclerView.setAdapter(productAdapter);

        categoryReference = FirebaseDatabase.getInstance().getReference("Categories");
        productReference = FirebaseDatabase.getInstance().getReference("Products");

        loadCategories();

        categoryAdapter.setOnItemClickListener(categoryId -> {
            Log.d("AdminHomeFragment", "Category clicked: " + categoryId);

            Fragment productFragment = ProductFragment.newInstance(categoryId);
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.admin_fragmentContainer, productFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadCategories() {
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    if (category != null) {
                        category.setCategoryId(postSnapshot.getKey()); // Set the categoryId
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();


                loadPopularProducts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminHomeFragment", "Failed to load categories", error.toException());
            }
        });
    }


    private void loadPopularProducts() {
        productReference.orderByChild("popularProduct").equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.e("AdminHomeFragment", "Failed to load popular products", error.toException());
            }
        });
    }
}
