package com.example.alibabarguha;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AdminAddCategoriesFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText categoryEditText;
    private ImageButton categoryImageButton;
    private Button addButton;
    private ImageView categoryImageView;
    private ProgressBar progressBar;

    private Uri imageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AdminAddCategoriesFragment() {

    }

    public static AdminAddCategoriesFragment newInstance(String param1, String param2) {
        AdminAddCategoriesFragment fragment = new AdminAddCategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        storageReference = FirebaseStorage.getInstance().getReference("CategoryImages");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_add_categories, container, false);

        categoryEditText = view.findViewById(R.id.categoryEdittext);
        categoryImageButton = view.findViewById(R.id.categoryImageButton);
        addButton = view.findViewById(R.id.button);
        categoryImageView = view.findViewById(R.id.categoryImageView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        categoryImageButton.setOnClickListener(v -> openFileChooser());

        addButton.setOnClickListener(v -> addCategory());

        return view;
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
            categoryImageView.setImageURI(imageUri);
        }
    }

    private void addCategory() {
        String categoryName = categoryEditText.getText().toString().trim();

        if (categoryName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                String categoryId = databaseReference.push().getKey();

                if (categoryId != null) {
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("name", categoryName);
                    categoryData.put("imageUrl", imageUrl);

                    databaseReference.child(categoryId).setValue(categoryData).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                            categoryEditText.setText("");  // Clear the input field
                            categoryImageView.setImageResource(0); // Clear the image view
                        } else {
                            Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
    }
}
