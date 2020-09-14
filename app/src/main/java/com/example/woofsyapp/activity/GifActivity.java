package com.example.woofsyapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.woofsyapp.R;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.dao.LikesDao;
import com.example.woofsyapp.model.RandomGifResponse;
import com.example.woofsyapp.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

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
    private Button btnRandom;
    public String randomDogImg;
    private ImageView IVShare, IVDownload, IVLike;
    private RandomGifResponse dataModel;
    private ProgressDialog dialog;
    private String breedName;
    public List<String> allImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        mContext = GifActivity.this;

        Intent intent = getIntent();
        randomDogImg = intent.getStringExtra("gifString");

        setIds();
        setListeners();

        dialog = new ProgressDialog(GifActivity.this);

        getSupportActionBar().setTitle("GIFS");

        if(randomDogImg == null){
            CallApiAsyncTask callApiAsynTask = new CallApiAsyncTask();
            callApiAsynTask.execute();
        }else{
            dialog.setMessage("Loading..");
            dialog.show();
            changeImage(randomDogImg);
        }

        allImages = new LinkedList<>();
        allImages = new LikesDao(this).getLikesList();

        if(allImages.contains(randomDogImg)){
            IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
        }else{
            IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
        }

    }

    private void setListeners() {
        IVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });

        IVDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isNetworkAvailable(mContext)){
                    DownloadImage(randomDogImg);
                }else{
                    showToast("Not connected to the Internet");
                }
            }
        });

        IVLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                allImages = new LinkedList<>();
                allImages = new LikesDao(GifActivity.this).getLikesList();

                if(allImages.contains(randomDogImg)){
                    IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
                    showToast("Photo unliked.");
                }else{
                    IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    showToast("Photo liked");
                }
                new MainActivity().updateLikedImages(randomDogImg, mContext);
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
                CallApiAsyncTask callApiAsynTask = new CallApiAsyncTask();
                callApiAsynTask.execute();

            }
        });
    }

    private void setIds() {
        ivDog = findViewById(R.id.iv_dog_img);
        progressBar = findViewById(R.id.progress_load_photo);
        btnRandom = findViewById(R.id.btn_random);
        IVShare = findViewById(R.id.iv_share);
        IVDownload = findViewById(R.id.iv_download);
        IVLike = findViewById(R.id.iv_like);
    }

    class CallApiAsyncTask extends AsyncTask<Void, Void, Boolean>{

        private CallApiAsyncTask(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading..");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            callApi2();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean val) {
            super.onPostExecute(val);
        }
    }

    private void shareImage() {
        //Try this for sharing picture as well
        //Currently, we're only sharing link

        breedName = "";
        String[] sArr = randomDogImg.split("/");
        breedName = sArr[4];

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this " + "Gif on this link ❤ " + randomDogImg);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    void DownloadImage(String ImageUrl) {

        if (checkPermission()){
          //  showToast("Downloading Image...");
            //Asynctask to create a thread to download image in the background
            new DownloadImage().execute(randomDogImg);
        } else {
            showToast("Need Permission to access storage for Downloading Gif");
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
            showToast("Downloading..");
        }

        @Override
        protected String doInBackground(String... url) {

            try {
                File imageStorageDir = new File(Environment.DIRECTORY_DOWNLOADS);
                if (!imageStorageDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    imageStorageDir.mkdirs();
                }
                // default image extension

                String date = DateFormat.getDateTimeInstance().format(new Date());
                String file = getString(R.string.app_name) + "-image-" + date.replace(" ", "").replace(":", "").replace(".", "") + ".gif";

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(url[0]);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file);

            dm.enqueue(request);

        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            // just in case, it should never be called anyway
            ex.printStackTrace();
        }

            return url[0];
        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
            showToast("Gif Saved");
        }
    }

    void showToast(String msg){
        Toast.makeText(GifActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void callApi2() {
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
                if (response.isSuccessful() && response.body() != null) {
                    dataModel = response.body();
                    randomDogImg = dataModel.getData().get(0).getImages().getOriginal().getUrl();
                    changeImage(randomDogImg);
                }
            }

            @Override
            public void onFailure(Call<RandomGifResponse> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeImage(String randomDogImg) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(R.drawable.ic_dog_playing);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(randomDogImg)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDog);
    }
}
