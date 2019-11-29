package com.example.tours;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.tours.Model.AuthRegister;
import com.example.tours.ui.home.HomeFragment;
import com.example.tours.ui.usertrip.UserTripFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = getSupportActionBar();
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_user_trip, R.id.navigation_map, R.id.navigation_notifications, R.id.navigation_user_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        toolbar.setTitle(R.string.title_home);
                        return true;
                    case R.id.navigation_history:
                        fragment = new UserTripFragment();
                        loadFragment(fragment);
                        toolbar.setTitle(R.string.title_user_trip);
                        return true;
                    case R.id.navigation_map:
                        toolbar.setTitle(R.string.title_map);
                        return true;
                    case R.id.navigation_notifications:
                        toolbar.setTitle(R.string.title_notifications);
                        return true;
                    case R.id.navigation_user_settings:
                        toolbar.setTitle(R.string.title_user_settings);
                        return true;
                }
                return false;
            }
        });

        // from register:
//        AuthRegister authRegisterOb = (AuthRegister) getIntent().getSerializableExtra("AuthRegister");


    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
