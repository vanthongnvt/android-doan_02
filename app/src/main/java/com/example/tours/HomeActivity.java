package com.example.tours;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.tours.Model.AuthRegister;
import com.example.tours.ui.home.HomeFragment;
import com.example.tours.ui.map.MapFragment;
import com.example.tours.ui.notifications.NotificationsFragment;
import com.example.tours.ui.usersettings.UserSettingsFragment;
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
    private FragmentTransaction transaction;
    private int idMenuSelected=1;
    private BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = getSupportActionBar();
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_user_trip, R.id.navigation_map, R.id.navigation_notifications, R.id.navigation_user_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
//        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment,R.string.title_home);
                        idMenuSelected=1;
                        return true;
                    case R.id.navigation_history:
                        fragment = new UserTripFragment();
                        loadFragment(fragment,R.string.title_user_trip);
                        idMenuSelected=2;
                        return true;
                    case R.id.navigation_map:
                        fragment = new MapFragment();
                        loadFragment(fragment,R.string.title_map);
                        idMenuSelected=3;
                        return true;
                    case R.id.navigation_notifications:
                        fragment= new NotificationsFragment();
                        loadFragment(fragment,R.string.title_notifications);
                        idMenuSelected=4;
                        return true;
                    case R.id.navigation_user_settings:
                        fragment = new UserSettingsFragment();
                        loadFragment(fragment,R.string.title_user_settings);
                        idMenuSelected=5;
                        return true;
                }
                return false;
            }
        });

        // from register:
//        AuthRegister authRegisterOb = (AuthRegister) getIntent().getSerializableExtra("AuthRegister");


    }
    public void setTitleBar(String title){
        toolbar.setTitle(title);
    }
    private void loadFragment(Fragment fragment, int resId) {
        // load fragment
        toolbar.setTitle(resId);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
//        transaction.addToBackStack(getString(resId));
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(idMenuSelected!=1) {
            navView.setSelectedItemId(R.id.navigation_home);
            idMenuSelected=1;
        }
        else{
            super.onBackPressed();
        }
    }
}
