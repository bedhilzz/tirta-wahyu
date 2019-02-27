package com.tirtawahyu.views.admin;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tirtawahyu.R;
import com.tirtawahyu.util.Loading;
import com.tirtawahyu.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PriceFragment.OnListFragmentInteractionListener,
        Loading {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.tvLoading)
    TextView tvLoading;

    @BindView(R.id.content_admin)
    ConstraintLayout adminLayout;

    TextView tvNavHeaderTitle;
    TextView tvNavHeaderSubtitle;

    final FirebaseAuth mAuth= FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        tvNavHeaderTitle = headerView.findViewById(R.id.tv_nav_header_title);
        tvNavHeaderSubtitle = headerView.findViewById(R.id.tv_nav_header_subtitle);

        String creationDate = Util.formatDate(mAuth.getCurrentUser().getMetadata().getCreationTimestamp());
        tvNavHeaderTitle.setText(mAuth.getCurrentUser().getDisplayName());
        tvNavHeaderSubtitle.setText(creationDate);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_statistic);

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_statistic));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = StatisticFragment.class;
        String titleBar = "";

        if (id == R.id.nav_price_management) {
            titleBar = getString(R.string.menu_price_management);
        } else if (id == R.id.nav_statistic) {
            titleBar = getString(R.string.menu_statistic);
            fragmentClass = StatisticFragment.class;
        } else if (id == R.id.nav_user_management) {
            titleBar = getString(R.string.menu_user_management);
            fragmentClass = UserManagementFragment.class;
        }
        toolbar.setTitle(titleBar);
        setTitle(titleBar);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_admin, fragment).commit();
        return true;
    }

    @Override
    public void onListFragmentInteraction(DocumentSnapshot item) {

    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        adminLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
        adminLayout.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
