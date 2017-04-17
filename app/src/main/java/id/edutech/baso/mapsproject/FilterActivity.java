package id.edutech.baso.mapsproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import id.edutech.baso.mapsproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLaplacianFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSmoothToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

public class FilterActivity extends AppCompatActivity {
    Bitmap bitmap, preview;
    GPUImage mGPUImage;

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
    //private final int FILTER_HARDLIGHT_BLEND = 22;
    private final int FILTER_HAZE = 23;
    private final int FILTER_GLASS_SPHERE = 24;
    private final int FILTER_EXPOSURE = 25;
    private final int FILTER_EMBOSS = 26;
    private final int FILTER_BOX_BLUR = 27;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Set Filter");
        ArrayList<Filter> filters = new ArrayList<>();
        String urlPhoto = Preferences.getStringPreferences("postUri",getApplicationContext());
        bitmap = null;
        preview=null;
        Bitmap resized=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(urlPhoto));
            //bitmap = cropCenter(bitmap);
            preview = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.5), (int) (bitmap.getHeight()*0.5), true);
            resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.1), (int) (bitmap.getHeight()*0.1), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.foto));
        mGPUImage.setImage(preview);
        resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.1), (int) (bitmap.getHeight()*0.1), true);
        filters.add(new Filter("Normal",FILTER_NORMAL));
        filters.add(new Filter("Sepia",FILTER_SEPIA));
        filters.add(new Filter("Hue",FILTER_HUE));
        filters.add(new Filter("Guassian",FILTER_GAUSSIAN));
        //filters.add(new Filter("Brightness",FILTER_BRIGHTNESS));
        filters.add(new Filter("Grayscale",FILTER_GRAYSCALE));
        //filters.add(new Filter("White Balance",FILTER_WHITE_BALANCE));
        filters.add(new Filter("Vignette",FILTER_VIGNETTE));
        //filters.add(new Filter("Transform",FILTER_TRANSFORM));
        filters.add(new Filter("TOON",FILTER_TOON));
        filters.add(new Filter("Swirl",FILTER_SWIRL));
        filters.add(new Filter("Smooth Toon",FILTER_SMOOTH_TOON));
        //filters.add(new Filter("Sharpen",FILTER_SHARPEN));
        //filters.add(new Filter("Screen Blend",FILTER_SCREEN_BLEND));
        //filters.add(new Filter("Saturation",FILTER_SATURATION));
        //filters.add(new Filter("RGB",FILTER_RGB));
        //filters.add(new Filter("Posterize",FILTER_POSTERIZE));
        //filters.add(new Filter("Pixelation",FILTER_PIXELATION));
        //filters.add(new Filter("Opacity",FILTER_OPACITY));
        filters.add(new Filter("Laplacian",FILTER_LAPLACIAN));
        filters.add(new Filter("Kuwahara",FILTER_KUWAHARA));
        //filters.add(new Filter("Highlight Shadow",FILTER_HIGHLIGHT_SHADOW));
        //filters.add(new Filter("Hardlight Blend",FILTER_HARDLIGHT_BLEND));
        filters.add(new Filter("Haze",FILTER_HAZE));
        filters.add(new Filter("Glass Sphere",FILTER_GLASS_SPHERE));
        filters.add(new Filter("Exposure",FILTER_EXPOSURE));
        filters.add(new Filter("Emboss",FILTER_EMBOSS));
        filters.add(new Filter("Box Blur",FILTER_BOX_BLUR));

        final ArrayList<Bitmap> thumbnails = new ArrayList<>();

        ArrayList<GPUImageFilter> thumbnailsFilter = new ArrayList<>();
        thumbnailsFilter.add(new GPUImageFilter());
        thumbnailsFilter.add(new GPUImageSepiaFilter());
        thumbnailsFilter.add(new GPUImageHueFilter());
        thumbnailsFilter.add(new GPUImageGaussianBlurFilter());
        thumbnailsFilter.add(new GPUImageGrayscaleFilter());
        thumbnailsFilter.add(new GPUImageVignetteFilter());
        thumbnailsFilter.add(new GPUImageToonFilter());
        thumbnailsFilter.add(new GPUImageSwirlFilter());
        thumbnailsFilter.add(new GPUImageSmoothToonFilter());
        thumbnailsFilter.add(new GPUImageLaplacianFilter());
        thumbnailsFilter.add(new GPUImageKuwaharaFilter());
        thumbnailsFilter.add(new GPUImageHazeFilter());
        thumbnailsFilter.add(new GPUImageGlassSphereFilter());
        thumbnailsFilter.add(new GPUImageExposureFilter());
        thumbnailsFilter.add(new GPUImageEmbossFilter());
        thumbnailsFilter.add(new GPUImageBoxBlurFilter());

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mGPUImage.getBitmapForMultipleFilters(resized,thumbnailsFilter, new GPUImage.ResponseListener<Bitmap>() {
            @Override
            public void response(Bitmap bitmap) {
                thumbnails.add(bitmap);
            }
        });


        FilterAdapter adapter = new FilterAdapter(getApplicationContext(),filters,thumbnails);
        final RecyclerView myList = (RecyclerView) findViewById(R.id.list_filter);
        adapter.setOnFilterSelectedListener(new FilterAdapter.OnFilterSelectedListener() {
            @Override
            public void onSelected(Filter filter) {
                switchFilter(filter.getFilterCode());
            }
        });

        myList.setLayoutManager(layoutManager);
        myList.setAdapter(adapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.next:
                Bitmap hasil = mGPUImage.getBitmapWithFilterApplied();
                saveImage("post.png",hasil);

                break;
            case android.R.id.home:
                    onBackPressed();
                break;
            }
        return super.onOptionsItemSelected(item);
    }
    private void switchFilter(final int filterCode){
        final Bitmap finalPreview = preview;
        new Thread(new Runnable() {
            @Override
            public void run() {
                 // this loads image on the current thread, should be run in a thread


                switch (filterCode){
                    case FILTER_NORMAL:
                        mGPUImage.setFilter(new GPUImageFilter());
                        break;
                    case FILTER_SEPIA:
                        mGPUImage.setFilter(new GPUImageSepiaFilter());
                        break;
                    case FILTER_HUE:
                        mGPUImage.setFilter(new GPUImageHueFilter());
                        break;
                    case FILTER_GAUSSIAN :
                        mGPUImage.setFilter(new GPUImageGaussianBlurFilter());
                        break;
                    /*case FILTER_BRIGHTNESS :
                        mGPUImage.setFilter(new GPUImageBrightnessFilter());
                        break;
                       */
                    case FILTER_GRAYSCALE :
                        mGPUImage.setFilter(new GPUImageGrayscaleFilter());
                        break;
                    /*case FILTER_WHITE_BALANCE :
                        mGPUImage.setFilter(new GPUImageWhiteBalanceFilter());
                        break;
                        */
                    case FILTER_VIGNETTE :
                        mGPUImage.setFilter(new GPUImageVignetteFilter());
                        break;
                    /*case FILTER_TRANSFORM :
                        mGPUImage.setFilter(new GPUImageTransformFilter());
                        break;
                        */
                    case FILTER_TOON :
                        mGPUImage.setFilter(new GPUImageToonFilter());
                        break;
                    case FILTER_SWIRL :
                        mGPUImage.setFilter(new GPUImageSwirlFilter());
                        break;
                    case FILTER_SMOOTH_TOON :
                        mGPUImage.setFilter(new GPUImageSmoothToonFilter());
                        break;

                    /*case FILTER_SHARPEN :
                        mGPUImage.setFilter(new GPUImageSharpenFilter());
                        break;

                    case FILTER_SCREEN_BLEND :
                        mGPUImage.setFilter(new GPUImageScreenBlendFilter());
                        break;
                    case FILTER_SATURATION :
                        mGPUImage.setFilter(new GPUImageSaturationFilter());
                        break;
                    case FILTER_RGB :
                        mGPUImage.setFilter(new GPUImageRGBFilter());
                        break;
                    case FILTER_POSTERIZE :
                        mGPUImage.setFilter(new GPUImagePosterizeFilter());
                        break;
                    case FILTER_PIXELATION :
                        mGPUImage.setFilter(new GPUImagePixelationFilter());
                        break;
                    case FILTER_OPACITY :
                        mGPUImage.setFilter(new GPUImageOpacityFilter());
                        break;
                        */
                    case FILTER_LAPLACIAN :
                        mGPUImage.setFilter(new GPUImageLaplacianFilter());
                        break;
                    case FILTER_KUWAHARA :
                        mGPUImage.setFilter(new GPUImageKuwaharaFilter());
                        break;
                    /*
                    case FILTER_HIGHLIGHT_SHADOW :
                        mGPUImage.setFilter(new GPUImageHighlightShadowFilter());
                        break;

                    case FILTER_HARDLIGHT_BLEND :
                        mGPUImage.setFilter(new GPUImageHardLightBlendFilter());
                        break;
                        */
                    case FILTER_HAZE :
                        mGPUImage.setFilter(new GPUImageHazeFilter());
                        break;
                    case FILTER_GLASS_SPHERE :
                        mGPUImage.setFilter(new GPUImageGlassSphereFilter());
                        break;
                    case FILTER_EXPOSURE :
                        mGPUImage.setFilter(new GPUImageExposureFilter());
                        break;
                    case FILTER_EMBOSS :
                        mGPUImage.setFilter(new GPUImageEmbossFilter());
                        break;
                    case FILTER_BOX_BLUR :
                        mGPUImage.setFilter(new GPUImageBoxBlurFilter());
                        break;
                }
            }
        }).start();
    }
    private void saveImage(String FileName,final Bitmap bitmap) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        final File file = new File(path, FileName);
        Uri uri = Uri.fromFile(file);
        Preferences.setStringPreferences("filteredPhoto",uri.toString(),getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final FileOutputStream out = new FileOutputStream(file);
                    Bitmap filterPreview = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.5), (int) (bitmap.getHeight()*0.5), true);
                    filterPreview.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Preferences.getBooleanPreferences("posted",getApplicationContext())){
            Preferences.setBooleanPreferences("posted",false,getApplicationContext());
            onBackPressed();
        }
    }

    private Bitmap cropCenter(Bitmap srcBmp){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

}
