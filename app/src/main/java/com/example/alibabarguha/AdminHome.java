package com.example.alibabarguha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.alibabarguha.AdminAddCategoriesFragment;
import com.example.alibabarguha.AdminAddProductsFragment;
import com.example.alibabarguha.CartFragment;
import com.example.alibabarguha.HomePageFragment;
import com.example.alibabarguha.R;
import com.google.android.material.navigation.NavigationView;

public class AdminHome extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        drawerLayout = findViewById(R.id.admin_drawerlayout);
        navigationView = findViewById(R.id.admin_navigation_drawer);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        TextView drawerUserNAME = headerView.findViewById(R.id.drawerUserName);

        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");


        drawerUserEmail.setText(email);
        drawerUserNAME.setText(name);

        loadFragment(new HomePageFragment(), email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.drawer_admin_home) {
                    loadFragment(new HomePageFragment());
                } else if (id == R.id.add_categories) {
                    loadFragment(new AdminAddCategoriesFragment());
                } else if (id == R.id.add_product) {
                    loadFragment(new AdminAddProductsFragment());
                } else if (id == R.id.drawer_admin_cart) {
                    loadFragment(new CartFragment());
                }else if (id == R.id.drawer_admin_logout) {
                    startActivity(new Intent(AdminHome.this, Login.class));
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadFragment(Fragment fragment, String email) {

        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}