package com.example.woofsyapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.woofsyapp.R;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.model.Datum;
import com.example.woofsyapp.model.RandomGifResponse;
import com.example.woofsyapp.util.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

public class GifActivity extends AppCompatActivity {

    private ImageView ivDog;
    private Context mContext;
    private ProgressBar progressBar;
    private List<String> breedsList;
    private Button btnRandom;
    private ProgressDialog progressDialog;
    public String randomDogImg;
    private int flag = 0;
    private ImageView IVShare, IVDownload, IVLike;
    private int flagLike = 0;
    private RandomGifResponse dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        ivDog = findViewById(R.id.iv_dog_img);
        progressBar = findViewById(R.id.progress_load_photo);
        btnRandom = findViewById(R.id.btn_random);
        IVShare = findViewById(R.id.iv_share);
        IVDownload = findViewById(R.id.iv_download);
        IVLike = findViewById(R.id.iv_like);

        mContext = GifActivity.this;
        breedsList = new LinkedList<>();

        getSupportActionBar().setTitle("Gifs");

        IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));

        callApi2();

        IVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });

        IVDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadImage(randomDogImg);  //CHANGE THE DOWNLOAD FEATURE FOR GIF
            }
        });

        IVLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagLike++;
                if(flagLike%2 != 0){
                    IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                }
                else{
                    IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
                }

                List<String> list = new MainActivity().updateLikedImages(randomDogImg);
                new MainActivity().setImagesList(list);

                StringBuilder str = new StringBuilder();
                for(String s: list){
                    str.append(s);
                }

                if(str != null)
                    showToast("Liked images : " + str);

            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
                flagLike = 0;
                callApi2();
            }
        });

    }

    private void shareImage() {
        showToast("Will share in other apps");
    }

    void DownloadImage(String ImageUrl) {

        if (checkPermission()){
            showToast("Downloading Image...");
            //Asynctask to create a thread to downlaod image in the background
            new DownloadImage().execute(randomDogImg);
        } else {
            showToast("Need Permission to access storage for Downloading Image");
        }
    }

    //runtime storage permission
    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 16) {
            int index = 0;
            Map<String, Integer> permissionsMap = new HashMap<>();
            for (String permission : permissions) {
                permissionsMap.put(permission, grantResults[index]);
                index++;
            }

            if(permissionsMap.containsKey(Manifest.permission.WRITE_EXTERNAL_STORAGE) && permissionsMap.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                new DownloadImage().execute(randomDogImg);
            }
        }
    }

    public class DownloadImage extends AsyncTask<String, Integer, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showToast("Please wait");
        }

        @Override
        protected String doInBackground(String... url) {
            File direct = new File(Environment.getExternalStorageDirectory() + "/Woofsy");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(url[0]);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/Woofsy", "dog.jpg");

            mgr.enqueue(request);

            return url[0];
        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
            showToast("Image Saved");
        }
    }

    void showToast(String msg){
        Toast.makeText(GifActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void callApi2() {
        showLoadingDialog();

        Random random = new Random();
        int offset = random.nextInt(4000);
        String api_key = "fH0BpGnLRdA12yZYn2zETY4Ka4IBoeXz";

        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL_GIPHY)
                .addConverterFactory(GsonConverterFactory.create())  //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        //creating the api interface
        Api api = retrofit.create(Api.class);
        Call<RandomGifResponse> call = api.getRandomGif(offset,api_key);

        call.enqueue(new Callback<RandomGifResponse>() {
            @Override
            public void onResponse(Call<RandomGifResponse> call, Response<RandomGifResponse> response) {
                dismissLoadingDialog();
                if (response.isSuccessful() && response.body() != null) {
                    dataModel = response.body();
                    randomDogImg = dataModel.getData().get(0).getImages().getOriginal().getUrl();

                    changeImage(randomDogImg);

                }
            }

            @Override
            public void onFailure(Call<RandomGifResponse> call, Throwable t) {
                dismissLoadingDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeImage(String randomDogImg) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(randomDogImg)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDog);
    }

    private void showLoadingDialog() {
        progressDialog = ProgressDialog.show(mContext, null, this.getString(R.string.loading), false, false);
    }

    private void dismissLoadingDialog() {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            progressDialog = null;
        }
    }
}
