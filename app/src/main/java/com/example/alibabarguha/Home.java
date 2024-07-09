package com.example.alibabarguha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.admin_navigation_drawer);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView cart_icon = drawerLayout.findViewById(R.id.cart_icon);

        if (cart_icon == null) {
            Log.d("TAG", "cart_icon is null");
        } else {
            cart_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "Cart clicked");
                    loadFragment(new CartFragment());
                }
            });
        }


        ImageView toggleIcon = findViewById(R.id.toggle_icon);
        toggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView drawerUserEmail = headerView.findViewById(R.id.drawerUserEmail);
        TextView drawerUserName = headerView.findViewById(R.id.drawerUserName);

        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");

        drawerUserEmail.setText(email);
        drawerUserName.setText(name);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.drawer_admin_home) {
                    loadFragment(new HomePageFragment());
                } else if (id == R.id.drawer_cart) {
                    loadFragment(new CartFragment());
                } else if (id == R.id.drawer_orders) {
                    // Handle orders
                } else if (id == R.id.drawer_logout) {
                    startActivity(new Intent(Home.this, Login.class));
                    finish();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        loadFragment(new HomePageFragment());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
