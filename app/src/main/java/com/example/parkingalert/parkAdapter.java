package com.example.parkingalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class parkAdapter extends RecyclerView.Adapter<parkAdapter.ParkViewHolder> {

    Context context;
    List<ParkingSpace> list;

    public parkAdapter(Context context, List<ParkingSpace> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_space,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder holder, int position) {
        Bitmap image = decodeImage(list.get(position).getEncodedBitmapPhoto());
        holder.imageParking.setImageBitmap(image);

        try {
            String[] address = getCityAddress(list.get(position));
            holder.streetAddress.setText(address[0]);
            holder.cityAddress.setText(address[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class ParkViewHolder extends RecyclerView.ViewHolder{

        TextView streetAddress;
        TextView cityAddress;
        ImageView imageParking;

        public ParkViewHolder(@NonNull View itemView){
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ParkingItemActivity.class));
                }
            });

            streetAddress = itemView.findViewById(R.id.parkStreet);
            cityAddress = itemView.findViewById(R.id.parkCity);
            imageParking = itemView.findViewById(R.id.imageParking);
        }


    }



    private Bitmap decodeImage(String s){
        byte[] decodeStringBytes = android.util.Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeStringBytes,0,decodeStringBytes.length);
    }

    private String[] getCityAddress(ParkingSpace parkingSpace) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(context, Locale.getDefault());
        addresses = geocoder.getFromLocation(parkingSpace.getLatitude(),parkingSpace.getLongitude(),1);

        String[] cityAddress = new String[]{addresses.get(0).getThoroughfare(),addresses.get(0).getLocality()};


        return cityAddress;

    }


}
