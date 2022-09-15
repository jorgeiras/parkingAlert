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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Clase adaptador para el RecyclerView de la pestaña de Parkings
 */
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

        Date d = new Date(list.get(position).getTimeStamp());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        holder.timeStamp.setText(sdf.format(d));

        if(list.get(position).isPaymentArea() == true){
            holder.paymentArea.setText("Zona de pago");
        }
        else{
            holder.paymentArea.setText("Zona no de pago");
        }

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


    /**
     * Clase que rellena cada uno de los items del RecyclerView de plazas
     */
    class ParkViewHolder extends RecyclerView.ViewHolder{

        TextView streetAddress;
        TextView cityAddress;
        TextView timeStamp;
        TextView paymentArea;
        ImageView imageParking;

        public ParkViewHolder(@NonNull View itemView){
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itemActivity = new Intent(context, ParkingItemActivity.class);
                    int i = getAbsoluteAdapterPosition();

                    try {
                        String[] address = getCityAddress(list.get(i));
                        itemActivity.putExtra("streetAdress",address[0]);
                        itemActivity.putExtra("cityName",address[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    itemActivity.putExtra("docID",list.get(i).getDocID());
                    itemActivity.putExtra("UserID",list.get(i).getUserID());
                    itemActivity.putExtra("encodedBitMap",list.get(i).getEncodedBitmapPhoto());
                    itemActivity.putExtra("latitude",list.get(i).getLatitude());
                    itemActivity.putExtra("longitude",list.get(i).getLongitude());
                    itemActivity.putExtra("paymentArea", list.get(i).isPaymentArea());
                    itemActivity.putExtra("timeStamp",list.get(i).getTimeStamp());
                    context.startActivity(itemActivity);

                }
            });

            streetAddress = itemView.findViewById(R.id.parkStreet);
            cityAddress = itemView.findViewById(R.id.parkCity);
            imageParking = itemView.findViewById(R.id.imageParking);
            paymentArea = itemView.findViewById(R.id.parkPayment);
            timeStamp =itemView.findViewById(R.id.parkTimeStamp);

        }


    }


    /**
     * Funcion para decodificar la imagen y convertirla a un objeto BitMap
     * @param s String con los datos codificados de la imagen
     * @return Bitmap de la imagen
     */
    private Bitmap decodeImage(String s){
        byte[] decodeStringBytes = android.util.Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeStringBytes,0,decodeStringBytes.length);
    }

    /**
     * Función para obtener la direccion de la plaza
     * @param parkingSpace Objeto con la informacion de la plaza
     * @return array de String con la direccion de la calle y ciudad
     * @throws IOException
     */
    private String[] getCityAddress(ParkingSpace parkingSpace) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(context, Locale.getDefault());
        addresses = geocoder.getFromLocation(parkingSpace.getLatitude(),parkingSpace.getLongitude(),1);

        String[] cityAddress = new String[]{addresses.get(0).getThoroughfare(),addresses.get(0).getLocality()};


        return cityAddress;

    }


}
