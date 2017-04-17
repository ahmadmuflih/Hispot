package id.edutech.baso.mapsproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.APIService;
import models.Subscribe;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/23/2016.
 */
public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.Holder> {
    ArrayList <Subscribe> listSubscribe;
    private Context mContext;
    String api_key;
    private OnSubscribeSelectedListener onSubscribeSelectedListener;

    public SubscribeAdapter(Context mContext, ArrayList<Subscribe> listSubscribe) {
        this.mContext = mContext;
        this.listSubscribe = listSubscribe;
        api_key=Preferences.getStringPreferences("api_key",mContext);
    }

    public void setOnSubscribeSelectedListener(OnSubscribeSelectedListener onSubscribeSelectedListener) {
        this.onSubscribeSelectedListener = onSubscribeSelectedListener;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subscribe_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        final Subscribe subscribe = listSubscribe.get(position);
        holder.nama.setText(subscribe.getNamaUser());
        holder.keterangan.setText(subscribe.getKeterangan());
        cekStatus(subscribe,holder.button);
        ImageUtil.capture(subscribe.getFotoUser(), "", holder.imageView, new ImageUtil.ImageUtilListener() {
            @Override
            public void onDisplayed() {

            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                holder.button.setEnabled(false);
                holder.button.setText("Requesting..");
                Call<Validation> subscribeCall = APIService.service.subscribe(subscribe.getIdUser()+"",api_key);
                subscribeCall.enqueue(new Callback<Validation>() {
                    @Override
                    public void onResponse(Call<Validation> call, Response<Validation> response) {
                        holder.button.setEnabled(true);
                        if(response.isSuccessful()){
                            if(response.body().getStatus().equals("success")){
                                subscribe.setStatus(!subscribe.isStatus());
                            }
                        }
                        else{
                            Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                        cekStatus(subscribe,holder.button);
                    }

                    @Override
                    public void onFailure(Call<Validation> call, Throwable t) {

                    }
                });

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onSubscribeSelectedListener != null)
                    onSubscribeSelectedListener.onSelected(subscribe);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return listSubscribe.get(position).getIdUser();
    }

    @Override
    public int getItemCount() {
        return listSubscribe.size();
    }


    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView nama;
        TextView keterangan;
        Button button;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.foto_subscribe);
            nama = (TextView) itemView.findViewById(R.id.nama_subscribe);
            keterangan = (TextView) itemView.findViewById(R.id.keterangan_subscribe);
            button = (Button)itemView.findViewById(R.id.button_subscribe);
        }
    }
    public interface OnSubscribeSelectedListener{
        void onSelected(Subscribe subscribe);
    }
    public void cekStatus(Subscribe subscribe, Button button){
        if (subscribe.isStatus()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_check, mContext.getTheme()), null, null, null);
                button.setBackground(mContext.getResources().getDrawable(R.drawable.button_shape2,mContext.getTheme()));
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_check), null, null, null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(mContext.getResources().getDrawable(R.drawable.button_shape2));
                }
                else{
                    button.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_shape2));
                }
            }
            button.setText("SUBSCRIBED");
            button.setTextColor(Color.WHITE);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_add, mContext.getTheme()), null, null, null);
                button.setBackground(mContext.getResources().getDrawable(R.drawable.button_shape,mContext.getTheme()));
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_add), null, null, null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(mContext.getResources().getDrawable(R.drawable.button_shape));
                }
                else{
                    button.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_shape));
                }
            }
            button.setText("SUBSCRIBE");
            button.setTextColor(Color.parseColor("#00897B"));
        }
    }
}
