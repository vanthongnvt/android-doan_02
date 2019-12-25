package com.ygaps.travelapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.ygaps.travelapp.ui.explore.ExploreFragment;
import com.ygaps.travelapp.ui.home.HomeFragment;
import com.ygaps.travelapp.ui.notifications.NotificationsFragment;
import com.ygaps.travelapp.ui.usersettings.UserSettingsFragment;
import com.ygaps.travelapp.ui.usertrip.UserTripFragment;
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
    private int idMenuSelected = 1;
    private BottomNavigationView navView;

    private int menu = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = getSupportActionBar();
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));
        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_explore, R.id.navigation_user_trip, R.id.navigation_notifications, R.id.navigation_user_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // send tourId to Map fragment
        Intent intent = getIntent();
        if (intent.hasExtra("MENU")) {
            menu = intent.getIntExtra("MENU", -1);
            if (menu == R.id.navigation_history) {
                UserTripFragment fragobj = new UserTripFragment();
                navView.getMenu().getItem(2).setChecked(true);
                idMenuSelected = 2;
                loadFragment(fragobj, R.string.title_user_trip);
            }
        }

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment, R.string.title_home);
                        idMenuSelected = 1;
                        return true;
                    case R.id.navigation_explore:
                        fragment = new ExploreFragment();
                        loadFragment(fragment, R.string.title_explore);
                        idMenuSelected = 2;
                        return true;
                    case R.id.navigation_history:
                        fragment = new UserTripFragment();
                        loadFragment(fragment, R.string.title_user_trip);
                        idMenuSelected = 3;
                        return true;
                    case R.id.navigation_notifications:
                        fragment = new NotificationsFragment();
                        loadFragment(fragment, R.string.title_notifications);
                        idMenuSelected = 4;
                        return true;
                    case R.id.navigation_user_settings:
                        fragment = new UserSettingsFragment();
                        loadFragment(fragment, R.string.title_user_settings);
                        idMenuSelected = 5;
                        return true;
                }
                return false;
            }
        });


    }

    public void setTitleBar(String title) {
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
        if (idMenuSelected != 1) {
            navView.setSelectedItemId(R.id.navigation_home);
            idMenuSelected = 1;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            boolean isInviteNoti = bundle.getBoolean("isInviteNoti", false);
            if (isInviteNoti) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (!(fragment instanceof NotificationsFragment)) {
                    navView.setSelectedItemId(R.id.navigation_notifications);
                }
            }
        }
    }

    public BottomNavigationView getNavigation() {
        return navView;
    }
}
