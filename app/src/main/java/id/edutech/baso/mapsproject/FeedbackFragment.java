package id.edutech.baso.mapsproject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.io.IOException;

import models.APIService;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class FeedbackFragment extends Fragment{
    private final int CHOOSE_GALLERY=2;
    ImageView imageView;
    EditText nama,isi;
    View view;
    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeActivity)getActivity()).setSendFeedbackVisible(true);
        ((HomeActivity)getActivity()).setCaptureVisible(View.GONE);
        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ((ImageButton)view.findViewById(R.id.add_screenshot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        imageView = (ImageView) view.findViewById(R.id.image_screenshot);
        nama = (EditText)view.findViewById(R.id.input_nama);
        isi = (EditText)view.findViewById(R.id.input_feedback);
        return view;

    }
    public void validate(){
        if(nama.getText().toString().trim().equals("")){
            requestFocus(nama);
            Toast.makeText(getActivity(), "Please fill your name!", Toast.LENGTH_SHORT).show();
        }
        else if(isi.getText().toString().trim().equals("")){
            requestFocus(isi);
            Toast.makeText(getActivity(), "Please fill your feedback!", Toast.LENGTH_SHORT).show();
        }
        else{
            String api_key = Preferences.getStringPreferences("api_key",getActivity());
            Call<Validation> feedbackCall = APIService.service.addFeedback(nama.getText().toString(),isi.getText().toString(),api_key);
            feedbackCall.enqueue(new Callback<Validation>() {
                @Override
                public void onResponse(Call<Validation> call, Response<Validation> response) {
                    if(response.isSuccessful()){
                        if(response.body().getStatus().equals("success")){
                            nama.setText("");
                            isi.setText("");
                            Toast.makeText(getActivity(), "Your feedback has been sent, thank you!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Validation> call, Throwable t) {
                    Toast.makeText(getActivity(), "Oops", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        ((HomeActivity)getActivity()).setSendFeedbackVisible(false);
        ((HomeActivity)getActivity()).setCaptureVisible(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==getActivity().RESULT_OK){
            Bitmap bitmap=null;
            if(requestCode==CHOOSE_GALLERY){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bitmap!=null){
                imageView.setVisibility(View.VISIBLE);
                ((ImageButton)view.findViewById(R.id.add_screenshot)).setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), "Terjadi kesalahan saat mengambil gambar!", Toast.LENGTH_LONG).show();
            }
        }
        ((HomeActivity)getActivity()).setSendFeedbackVisible(true);
        ((HomeActivity)getActivity()).setCaptureVisible(View.GONE);
    }

    private void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"Pilih Foto"),CHOOSE_GALLERY);
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}