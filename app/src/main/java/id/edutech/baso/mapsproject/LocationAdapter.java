package id.edutech.baso.mapsproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.Location;

/**
 * Created by Baso on 10/23/2016.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Holder> {
    ArrayList <Location> listLocation = new ArrayList<>();
    private Context mContext;
    private OnLocationSelectedListener onLocationSelectedListener;

    public LocationAdapter(Context mContext, ArrayList<Location> listLocation) {
        this.mContext = mContext;
        this.listLocation = listLocation;
    }

    public void setOnLocationSelectedListener(OnLocationSelectedListener onLocationSelectedListener) {
        this.onLocationSelectedListener = onLocationSelectedListener;
    }

    public void setEmpty(){
        listLocation=new ArrayList<>();
        notifyDataSetChanged();
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.location_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Location location = listLocation.get(position);
        holder.nama.setText(location.getLocationName());
        holder.keterangan.setText(location.getLocationDistance());
          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onLocationSelectedListener != null)
                    onLocationSelectedListener.onSelected(location);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return listLocation.get(position).getLocationId();
    }

    @Override
    public int getItemCount() {
        return listLocation.size();
    }


    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        TextView nama;
        TextView keterangan;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nama = (TextView) itemView.findViewById(R.id.location_name);
            keterangan = (TextView) itemView.findViewById(R.id.location_bio);
        }
    }
    public interface OnLocationSelectedListener{
        void onSelected(Location location);
    }
}
