package id.edutech.baso.mapsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

/**
 * Created by Baso on 11/8/2016.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Holder>{
    ArrayList<Filter> listFilter;
    ArrayList<Bitmap> thumbnails;
    private Context mContext;
    private OnFilterSelectedListener onFilterSelectedListener;
    private Filter selectedFilter;

    private final int FILTER_NORMAL = 0;
    private final int FILTER_SEPIA = 1;
    private final int FILTER_HUE = 2;
    private final int FILTER_GAUSSIAN = 3;
    //private final int FILTER_BRIGHTNESS = 4;
    private final int FILTER_GRAYSCALE = 5;
    //private final int FILTER_WHITE_BALANCE = 6;
    private final int FILTER_VIGNETTE = 7;
    //private final int FILTER_TRANSFORM = 8;
    private final int FILTER_TOON = 9;
    private final int FILTER_SWIRL = 10;
    private final int FILTER_SMOOTH_TOON = 11;
    //private final int FILTER_SHARPEN = 12;
    //private final int FILTER_SCREEN_BLEND = 13;
    //private final int FILTER_SATURATION = 14;
    //private final int FILTER_RGB = 15;
    //private final int FILTER_POSTERIZE = 16;
    //private final int FILTER_PIXELATION = 17;
    //private final int FILTER_OPACITY = 18;
    private final int FILTER_LAPLACIAN = 19;
    private final int FILTER_KUWAHARA = 20;
    //private final int FILTER_HIGHLIGHT_SHADOW = 21;
    private final int FILTER_HARDLIGHT_BLEND = 22;
    private final int FILTER_HAZE = 23;
    private final int FILTER_GLASS_SPHERE = 24;
    private final int FILTER_EXPOSURE = 25;
    private final int FILTER_EMBOSS = 26;
    private final int FILTER_BOX_BLUR = 27;

    public FilterAdapter(Context mContext, ArrayList<Filter> listFilter, ArrayList<Bitmap> thumbnails) {
        this.listFilter = listFilter;
        this.thumbnails = thumbnails;
        this.mContext = mContext;
    }

    public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
        this.onFilterSelectedListener = onFilterSelectedListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.filter_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final Filter filter = listFilter.get(position);
        holder.nama.setText(filter.getFilterName());
        holder.imageView.setImageBitmap(thumbnails.get(position));
        if(filter==selectedFilter){
            holder.itemView.setBackgroundColor(Color.parseColor("#AAE9E9E9"));
            holder.nama.setTypeface(null, Typeface.BOLD);
            holder.nama.setTextColor(Color.BLACK);
        }
        else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.nama.setTypeface(null, Typeface.NORMAL);
            holder.nama.setTextColor(Color.GRAY);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedFilter = filter;
                if(onFilterSelectedListener != null)
                    onFilterSelectedListener.onSelected(filter);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }


    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView nama;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.filter_photo);
            nama = (TextView) itemView.findViewById(R.id.filter_name);
        }
    }
    public interface OnFilterSelectedListener{
        void onSelected(Filter filter);
    }
}
