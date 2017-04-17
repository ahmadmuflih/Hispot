package id.edutech.baso.mapsproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.Subscribe;

/**
 * Created by Baso on 10/23/2016.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.Holder> {
    ArrayList <Subscribe> listPeople = new ArrayList<>();
    private Context mContext;
    private OnPeopleSelectedListener onPeopleSelectedListener;

    public PeopleAdapter(Context mContext, ArrayList<Subscribe> listPeople) {
        this.mContext = mContext;
        this.listPeople = listPeople;
    }

    public void setOnPeopleSelectedListener(OnPeopleSelectedListener onPeopleSelectedListener) {
        this.onPeopleSelectedListener = onPeopleSelectedListener;
    }

    public void setEmpty(){
        listPeople=new ArrayList<>();
        notifyDataSetChanged();
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.people_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Subscribe people = listPeople.get(position);
        holder.nama.setText(people.getNamaUser());
        holder.keterangan.setText(people.getKeterangan());
        ImageUtil.capture(people.getFotoUser(), "", holder.imageView, new ImageUtil.ImageUtilListener() {
            @Override
            public void onDisplayed() {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPeopleSelectedListener != null)
                    onPeopleSelectedListener.onSelected(people);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return listPeople.get(position).getIdUser();
    }

    @Override
    public int getItemCount() {
        return listPeople.size();
    }


    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView nama;
        TextView keterangan;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.people_photo);
            nama = (TextView) itemView.findViewById(R.id.people_name);
            keterangan = (TextView) itemView.findViewById(R.id.people_bio);
        }
    }
    public interface OnPeopleSelectedListener{
        void onSelected(Subscribe People);
    }
}
