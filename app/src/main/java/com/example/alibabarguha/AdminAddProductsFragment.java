package com.example.alibabarguha;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdminAddProductsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AdminAddProductsFragment";

    private EditText productNameEditText, productQuantityEditText, productPriceEditText;
    private Spinner categorySpinner;
    private RadioGroup radioGroup;
    private Button addProductButton;
    private ImageView selectImageButton;
    private ImageView productImageView;

    private Uri imageUri;

    private DatabaseReference categoriesReference, productsReference;
    private StorageReference storageReference;

    private List<String> categoryList = new ArrayList<>();
    private HashMap<String, String> categoryMap = new HashMap<>();

    public AdminAddProductsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_add_products, container, false);


        productNameEditText = view.findViewById(R.id.productNameEditText);
        productQuantityEditText = view.findViewById(R.id.productQuantityEditText);
        productPriceEditText = view.findViewById(R.id.productPriceEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        radioGroup = view.findViewById(R.id.radioGroup);
        addProductButton = view.findViewById(R.id.addProductButton);
        selectImageButton = view.findViewById(R.id.selectImageButton);


        categoriesReference = FirebaseDatabase.getInstance().getReference("Categories");
        productsReference = FirebaseDatabase.getInstance().getReference("Products");
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages");


        loadCategories();


        selectImageButton.setOnClickListener(v -> openFileChooser());
        addProductButton.setOnClickListener(v -> addProduct());

        return view;
    }

    private void loadCategories() {
        categoriesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                categoryMap.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryId = categorySnapshot.getKey();
                    String categoryName = categorySnapshot.child("name").getValue(String.class);
                    if (categoryId != null && categoryName != null) {
                        categoryList.add(categoryName);
                        categoryMap.put(categoryName, categoryId);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectImageButton.setImageURI(imageUri);
        }
    }

    private void addProduct() {
        String productName = productNameEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String productPrice = productPriceEditText.getText().toString().trim();
        String selectedCategory = (String) categorySpinner.getSelectedItem();
        int popularProduct = getSelectedRadioValue();

        if (productName.isEmpty() || productQuantity.isEmpty() || productPrice.isEmpty() || selectedCategory == null) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryMap.get(selectedCategory);
        if (categoryId == null) {
            Toast.makeText(getContext(), "Invalid category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageAndSaveProduct(productName, productQuantity, productPrice, categoryId, popularProduct);
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSelectedRadioValue() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = getView().findViewById(selectedId);
        if (selectedRadioButton != null && selectedRadioButton.getText().equals("Yes")) {
            return 1;
        } else {
            return 0;
        }
    }

    private void uploadImageAndSaveProduct(String productName, String productQuantity, String productPrice, String categoryId, int popularProduct) {
        StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveProductToDatabase(productName, productQuantity, productPrice, imageUrl, categoryId, popularProduct);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get download URL", e);
                    Toast.makeText(getContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image", e);
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProductToDatabase(String productName, String productQuantity, String productPrice, String imageUrl, String categoryId, int popularProduct) {
        String productId = productsReference.push().getKey();
        if (productId != null) {
            Product product = new Product(productName, productQuantity, productPrice, imageUrl, categoryId, popularProduct);
            productsReference.child(productId).setValue(product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearFields() {
        productNameEditText.setText("");
        productQuantityEditText.setText("");
        productPriceEditText.setText("");
        selectImageButton.setImageResource(0);
        radioGroup.clearCheck();
    }

    public static class Product {
        private String productName;
        private String productQuantity;
        private String productPrice;
        private String productImageUrl;
        private String categoryId;
        private int popularProduct;



        public Product(String productName, String productQuantity, String productPrice, String productImageUrl, String categoryId, int popularProduct) {
            this.productName = productName;
            this.productQuantity = productQuantity;
            this.productPrice = productPrice;
            this.productImageUrl = productImageUrl;
            this.categoryId = categoryId;
            this.popularProduct = popularProduct;
        }


        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductQuantity() {
            return productQuantity;
        }

        public void setProductQuantity(String productQuantity) {
            this.productQuantity = productQuantity;
        }

        public String getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        public String getProductImageUrl() {
            return productImageUrl;
        }

        public void setProductImageUrl(String productImageUrl) {
            this.productImageUrl = productImageUrl;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public int getPopularProduct() {
            return popularProduct;
        }

        public void setPopularProduct(int popularProduct) {
            this.popularProduct = popularProduct;
        }
    }
}
