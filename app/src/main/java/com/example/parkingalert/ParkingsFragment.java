package com.example.parkingalert;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingsFragment extends Fragment {


    private RecyclerView recyclerView;
    FirebaseFirestore db;
    private double latitude;
    private double longitude;
    List<ParkingSpace> parkingSpaceList;
    parkAdapter parkAdapterObject;
    private SwipeRefreshLayout swipeRefreshLayout;
    LocationRequest locationRequest;
    int counter =0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ParkingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParkingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParkingsFragment newInstance(String param1, String param2) {
        ParkingsFragment fragment = new ParkingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_recent_parkings, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshRecent);

        getDeviceLocation();

        db = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.recyclerRecent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        parkingSpaceList = new ArrayList<>();
        parkAdapterObject = new parkAdapter(getActivity(), parkingSpaceList);
        recyclerView.setAdapter(parkAdapterObject);




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeviceLocation();
                filterParkings();
            }
        });


        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    private void getDeviceLocation() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder build = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        build.setAlwaysShow(true);

        Task<LocationSettingsResponse> response = LocationServices.getSettingsClient(getActivity().getApplicationContext()).checkLocationSettings(build.build());

        response.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse res = task.getResult(ApiException.class);
                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
                            } catch (IntentSender.SendIntentException exception) {
                                exception.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);

                if(locationResult!=null && locationResult.getLocations().size() > 0){
                    latitude = locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude();
                    longitude = locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude();
                    if(counter <0 && latitude!=0.0 && longitude != 0.0){
                        Log.i("ParkingActivity","CONTADOR 2");
                        filterParkings();
                        counter++;
                    }

                }
            }
        }, Looper.getMainLooper());


    }










    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {

        super.onResume();
        filterParkings();

    }




    @RequiresApi(api = Build.VERSION_CODES.S)
    private void filterParkings(){
        Log.i("ParkingActivity","COMENZANDO FILTRO");
        Log.i("ParkingActivity","latitud filtro:" + latitude+  "  longitud filtro: " + longitude);
        getDeviceLocation();
        recyclerView.setAdapter(parkAdapterObject);
        parkingSpaceList.clear();
        db.collection("Parkings").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Location actualLocation = new Location("");
                    actualLocation.setLatitude(latitude);
                    actualLocation.setLongitude(longitude);
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ParkingSpace p = document.toObject(ParkingSpace.class);
                        p.setDocID(document.getId());

                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("options", Context.MODE_PRIVATE);
                        int distance = sharedPreferences.getInt("distance",50);
                        String measure = sharedPreferences.getString("measure","m");

                        if(measure.equals("km")){
                            distance =  distance *1000;
                        }

                        Location newlocation = new Location("");
                        newlocation.setLatitude(p.getLatitude());
                        newlocation.setLongitude(p.getLongitude());

                        float result = actualLocation.distanceTo(newlocation);

                        if(result < distance){
                            Log.i("ParkingActivity","plaza introducida");
                            parkingSpaceList.add(p);
                        }
                    }
                    parkAdapterObject.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }


}