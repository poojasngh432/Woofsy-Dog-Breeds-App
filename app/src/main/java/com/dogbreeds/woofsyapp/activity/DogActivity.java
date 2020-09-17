package com.dogbreeds.woofsyapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dogbreeds.woofsyapp.R;
import com.dogbreeds.woofsyapp.dao.LikesDao;
import com.dogbreeds.woofsyapp.util.Utils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

public class DogActivity extends AppCompatActivity {
    private String imageAddress, breedType;
    private ImageView IVDog, IVShare, IVDownload, IVLike;
    private TextView breedNameTV;
    private Context mContext;
    private ProgressBar progressBar;
    public List<String> allImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog);
        mContext = DogActivity.this;

        Intent intent = getIntent();
        imageAddress = intent.getStringExtra("imageAddress");
        breedType = intent.getStringExtra("breedType");

        setIds();
        setListeners();

        if(breedType != null){
            getSupportActionBar().setTitle(breedType);
            breedNameTV.setText(breedType);
        }else{
            String[] arr = imageAddress.split("/");
            breedType = arr[4];
            breedNameTV.setText(breedType);
        }

        allImages = new LinkedList<>();
        allImages = new LikesDao(this).getLikesList();

        if(allImages.contains(imageAddress)){
            IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
        }else{
            IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(imageAddress)
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
                .into(IVDog);
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
                    DownloadImage(imageAddress);
                }else{
                    showToast("Not connected to the Internet");
                }
            }
        });

        IVLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                allImages = new LinkedList<>();
                allImages = new LikesDao(DogActivity.this).getLikesList();

                if(allImages.contains(imageAddress)){
                    IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike_red));
                    showToast("Photo unliked.");
                }else{
                    IVLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    showToast("Photo liked.");
                }
                new MainActivity().updateLikedImages(imageAddress, mContext);

            }
        });
    }

    private void setIds() {
        IVDog = findViewById(R.id.dog_imageview);
        progressBar = findViewById(R.id.progress_load_photo);
        IVShare = findViewById(R.id.iv_share);
        IVDownload = findViewById(R.id.iv_download);
        IVLike = findViewById(R.id.iv_like);
        breedNameTV = findViewById(R.id.name_of_breed_tv);
    }

    private void shareImage() {
        //Try this for sharing picture as well
        //Currently, we're only sharing link
        String breedName = "";
        String[] sArr = imageAddress.split("/");
        breedName = sArr[4];
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this " + breedName + " photo on this link ❤ " + imageAddress);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    void DownloadImage(String ImageUrl) {

        if (checkPermission()){
            showToast("Downloading Image...");
            //Asynctask to create a thread to downlaod image in the background
            new DownloadImage().execute(imageAddress);
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
                new DownloadImage().execute(imageAddress);
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

            DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(url[0]);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Image Download").setDescription("Downloading...")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, breedType + ".jpg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);;

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
        Toast.makeText(DogActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
