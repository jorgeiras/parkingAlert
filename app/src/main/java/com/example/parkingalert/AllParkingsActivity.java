package com.example.parkingalert;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.parkingalert.ui.main.SectionsPagerAdapter;
import com.example.parkingalert.databinding.ActivityAllParkingsBinding;

public class AllParkingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActivityAllParkingsBinding binding;
    private int permissionAccepted = 1;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAllParkingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("parkingAlert");

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        LocationPermission();
        isGPSEnabled();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(AllParkingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if(isGPSEnabled()){
                        startActivity(new Intent(AllParkingsActivity.this, AddParkingActivity.class));
                    }
                } else {
                    LocationPermission();
                    isGPSEnabled();
                    if(ContextCompat.checkSelfPermission(AllParkingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(isGPSEnabled()){
                            startActivity(new Intent(AllParkingsActivity.this, AddParkingActivity.class));
                        }
                    }
                }




            }
        });


    }


    private boolean isGPSEnabled(){
        final LocationManager m = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!m.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            new AlertDialog.Builder(this).setTitle("Activar GPS").setMessage("Active la localización del teléfono")
                    .setPositiveButton("activar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent in = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(in);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();


        }
        else{
            return true;
        }
        return false;

    }

    private void LocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this).setTitle("permiso necesario").setMessage("es necesario aceptar el permiso de localización")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(AllParkingsActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, permissionAccepted);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, permissionAccepted);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_item_options:{
                startActivity(new Intent(AllParkingsActivity.this, OptionsActivity.class));
                drawerLayout.closeDrawers();
                break;
            }
            case R.id.nav_item_logOut:{
                startActivity(new Intent(AllParkingsActivity.this, LoginActivity.class));
                drawerLayout.closeDrawers();
                finish();
                break;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }



}