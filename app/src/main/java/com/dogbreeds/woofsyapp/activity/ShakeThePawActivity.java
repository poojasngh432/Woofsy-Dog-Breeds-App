package com.dogbreeds.woofsyapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.dogbreeds.woofsyapp.api.Api;
import com.dogbreeds.woofsyapp.model.RandomDogModel;
import com.dogbreeds.woofsyapp.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShakeThePawActivity extends AppCompatActivity {

    private String correctAnswer;
    private ImageView ivDog;
    private Context mContext;
    private ProgressBar progressBar;
    private Button btnRandom;
    private ProgressDialog progressDialog;
    public String randomDogImg;
    private TextView breedNameTV;
    private String[] strArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_the_paw);
        mContext = ShakeThePawActivity.this;

        Intent intent = getIntent();
        randomDogImg = intent.getStringExtra("dogImage");

        setIds();

        if(randomDogImg != null){
            showLoadingDialog();
            strArr = randomDogImg.split("/");
            correctAnswer = strArr[4];
            breedNameTV.setText(correctAnswer);
            changeImage(randomDogImg);
        }else{
            if (Utils.isNetworkAvailable(mContext)) {
                callApi2();
            }else{
                showToast("Not connected to Internet");
            }
        }

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(mContext)) {
                    callApi2();
                }else{
                    showToast("Not connected to Internet");
                }
            }
        });

    }

    private void setIds() {
        ivDog = findViewById(R.id.iv_dog_img);
        progressBar = findViewById(R.id.progress_load_photo);
        btnRandom = findViewById(R.id.btn_random);
        breedNameTV = findViewById(R.id.name_of_breed_tv);
    }

    private void callApi2() {
        showLoadingDialog();
        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())  //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        //creating the api interface
        Api api = retrofit.create(Api.class);
        Call<RandomDogModel> call = api.getRandomDog();

        call.enqueue(new Callback<RandomDogModel>() {
            @Override
            public void onResponse(Call<RandomDogModel> call, Response<RandomDogModel> response) {
              //  dismissLoadingDialog();
                if (response.isSuccessful() && response.body() != null) {
                    randomDogImg = response.body().getMessage();
                    strArr = randomDogImg.split("/");
                    String breedName = strArr[4];
                    breedNameTV.setText(breedName);
                    changeImage(randomDogImg);

                }
            }

            @Override
            public void onFailure(Call<RandomDogModel> call, Throwable t) {
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
                        dismissLoadingDialog();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        dismissLoadingDialog();
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

    void showToast(String msg){
        Toast.makeText(ShakeThePawActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

}
