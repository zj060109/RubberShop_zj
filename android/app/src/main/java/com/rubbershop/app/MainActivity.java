package com.rubbershop.app;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rubbershop.app.data.local.TokenManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNav;
    private Toolbar toolbar;
    private boolean bottomNavConfigured = false;

    private static final List<Integer> HIDE_NAV = Arrays.asList(
            R.id.authFragment, R.id.productDetailFragment, R.id.orderDetailFragment,
            R.id.customCreateFragment, R.id.customDetailFragment,
            R.id.creditListFragment, R.id.merchantCreditFragment,
            R.id.chatDetailFragment);

    private static final Set<Integer> TOP_LEVEL = new HashSet<>(Arrays.asList(
            R.id.customerHomeFragment, R.id.customerOrderListFragment,
            R.id.customListFragment, R.id.customerChatFragment, R.id.customerProfileFragment,
            R.id.merchantProductListFragment, R.id.merchantOrderListFragment,
            R.id.merchantChatFragment, R.id.merchantProfileFragment,
            R.id.factoryPurchaseFragment, R.id.factoryProfileFragment,
            R.id.authFragment));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_nav);

        navController.setGraph(R.navigation.nav_graph);

        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(TOP_LEVEL).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);

        navController.addOnDestinationChangedListener((c, d, a) -> {
            int id = d.getId();
            if (id == R.id.authFragment) {
                bottomNav.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                bottomNavConfigured = false;
            } else if (HIDE_NAV.contains(id)) {
                bottomNav.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
            } else {
                toolbar.setVisibility(View.VISIBLE);
                if (!bottomNavConfigured) {
                    configureBottomNav();
                    bottomNavConfigured = true;
                }
                bottomNav.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void configureBottomNav() {
        String role = TokenManager.getRole();
        if (role == null) role = "customer";
        bottomNav.getMenu().clear();

        switch (role) {
            case "merchant":
                bottomNav.inflateMenu(R.menu.bottom_nav_merchant);
                break;
            case "factory":
                bottomNav.inflateMenu(R.menu.bottom_nav_factory);
                break;
            default:
                bottomNav.inflateMenu(R.menu.bottom_nav_customer);
                break;
        }

        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            boolean popped = false;
            while (getSupportFragmentManager().findFragmentByTag("chat_detail") != null) {
                getSupportFragmentManager().popBackStack();
                popped = true;
            }
            if (popped) {
                navController.navigate(itemId);
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
        bottomNavConfigured = true;
    }
}
