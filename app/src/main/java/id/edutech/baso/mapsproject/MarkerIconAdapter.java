package id.edutech.baso.mapsproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

/**
 * Created by Baso on 11/8/2016.
 */
public class MarkerIconAdapter extends RecyclerView.Adapter<MarkerIconAdapter.Holder>{
    ArrayList<MarkerIcon> listIcon;
    private Context mContext;
    private OnMarkerIconSelectedListener onMarkerIconSelectedListener;
    private OnMarkerCheckedListener onMarkerCheckedListener;

    public void setOnMarkerCheckedListener(OnMarkerCheckedListener onMarkerCheckedListener) {
        this.onMarkerCheckedListener = onMarkerCheckedListener;
    }

    public MarkerIconAdapter(Context mContext, ArrayList<MarkerIcon> listIcon) {
        this.mContext = mContext;
        this.listIcon = listIcon;
    }

    public void setOnMarkerIconSelectedListener(OnMarkerIconSelectedListener onMarkerIconSelectedListener) {
        this.onMarkerIconSelectedListener = onMarkerIconSelectedListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.marker_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final MarkerIcon markerIcon = listIcon.get(position);
        holder.nama.setText(markerIcon.getTypeName());
        holder.checkBox.setChecked(markerIcon.isChecked());
        holder.imageView.setImageBitmap(markerIcon.getIcon());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                markerIcon.setChecked(isChecked);
                if(onMarkerCheckedListener!= null)
                    onMarkerCheckedListener.onChecked(markerIcon);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onMarkerIconSelectedListener != null)
                    onMarkerIconSelectedListener.onSelected(markerIcon);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listIcon.size();
    }


    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView nama;
        CheckBox checkBox;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.marker_icon);
            nama = (TextView) itemView.findViewById(R.id.marker_name);
            checkBox=(CheckBox)itemView.findViewById(R.id.checkbox);
        }
    }
    public interface OnMarkerIconSelectedListener{
        void onSelected(MarkerIcon markerIcon);
    }
    public interface OnMarkerCheckedListener{
        void onChecked(MarkerIcon markerIcon);
    }
}
