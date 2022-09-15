package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

/**
 * Fragmento que crea una mapa de Google Maps para la actividad AddParkingActivity
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    FusedLocationProviderClient fusedLoc;
    private static double latitude;
    private static double longitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


        @Override
        public void onMapReady(GoogleMap googleMap) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);


            map = googleMap;
            getDeviceLocation();


        }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLoc = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);


    }


    /**
     * Funcion para obtener la ubicacion actual del telefono
     */
    private void getDeviceLocation() {

        try {

                Task<Location> locationResult = fusedLoc.getLastLocation();
                locationResult.addOnSuccessListener( new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Location lastKnownLocation = location;
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 16.0f));
                            map.addMarker(new MarkerOptions().position( new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())));
                            latitude = lastKnownLocation.getLatitude();
                            longitude = lastKnownLocation.getLongitude();

                        }
                    }


                });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    /**
     * Funcion para obtener la coordenada longitud
     * @return coordenada longitud
     */
    public static double getLong(){
            return longitude;
    }


    /**
     * Funcion para obtener la coordenada latitud
     * @return coordenada latitud
     */
    public static double getLat(){
        return latitude;
    }


}