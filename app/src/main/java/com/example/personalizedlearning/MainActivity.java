package com.example.personalizedlearning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.personalizedlearning.utils.ProfileManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavController navController;
    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private boolean navigationInitialized = false;
    // Flag to prevent navigation loops
    private boolean isRedirecting = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components with proper error handling
        setupNavigation();
    }
    
    private void setupNavigation() {
        try {
            // Initialize UI components
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            
            // Get NavController using the recommended way (avoid deprecated methods)
            FragmentManager fragmentManager = getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment) 
                    fragmentManager.findFragmentById(R.id.nav_host_fragment);
            
            if (navHostFragment == null) {
                Toast.makeText(this, "Navigation host fragment not found", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Get NavController from the NavHostFragment
            navController = navHostFragment.getNavController();
            
            // Configure action bar
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, 
                    R.id.navigation_learning,
                    R.id.navigation_assessment, 
                    R.id.navigation_profile,
                    R.id.navigation_sign_in,
                    R.id.navigation_sign_up)
                    .setOpenableLayout(drawerLayout)
                    .build();
            
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            
            // Setup navigation views
            if (navigationView != null) {
                navigationView.setNavigationItemSelectedListener(this);
            }
            
            if (bottomNavigationView != null) {
                NavigationUI.setupWithNavController(bottomNavigationView, navController);
            }
            
            // Add destination change listener
            navController.addOnDestinationChangedListener(this::onDestinationChanged);
            
            navigationInitialized = true;
        } catch (Exception e) {
            Toast.makeText(this, "Navigation setup error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        try {
            if (destination == null) return;
            
            int id = destination.getId();
            boolean isAuthScreen = (id == R.id.navigation_sign_in || id == R.id.navigation_sign_up);
            
            // Show/hide UI based on screen type
            if (getSupportActionBar() != null) {
                if (isAuthScreen) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }
            
            if (drawerLayout != null) {
                if (isAuthScreen) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                } else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
            
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(isAuthScreen ? View.GONE : View.VISIBLE);
            }
            
            // Check authentication for non-auth screens
            if (!isAuthScreen) {
                boolean isAuthenticated = false;
                try {
                    isAuthenticated = ProfileManager.getInstance(this).isProfileCreated();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // If not logged in and trying to access protected area, redirect once
                if (!isAuthenticated && !isRedirecting && 
                    (id == R.id.navigation_learning || 
                     id == R.id.navigation_assessment || 
                     id == R.id.navigation_profile)) {
                    
                    redirectToLogin();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void redirectToLogin() {
        if (isRedirecting) return;
        
        isRedirecting = true;
        mainHandler.postDelayed(() -> {
            try {
                if (navController != null && navigationInitialized) {
                    navController.navigate(R.id.navigation_sign_in);
                    Toast.makeText(MainActivity.this, 
                            "Please sign in to access this feature", 
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isRedirecting = false;
            }
        }, 200);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            if (!navigationInitialized || navController == null) {
                return false;
            }
            
            int id = item.getItemId();
            boolean isAuthenticated = false;
            
            try {
                isAuthenticated = ProfileManager.getInstance(this).isProfileCreated();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Check authentication for protected routes
            if (!isAuthenticated && 
                (id == R.id.navigation_learning || 
                 id == R.id.navigation_assessment || 
                 id == R.id.navigation_profile)) {
                
                redirectToLogin();
            } else {
                // Navigate to selected destination
                mainHandler.post(() -> {
                    try {
                        navController.navigate(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            
            // Close drawer after selection
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            if (navigationInitialized && navController != null) {
                return NavigationUI.navigateUp(navController, appBarConfiguration) || 
                       super.onSupportNavigateUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onSupportNavigateUp();
    }
}