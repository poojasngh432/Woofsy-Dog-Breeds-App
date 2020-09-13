package com.example.woofsyapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.woofsyapp.R;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.dao.AllBreedsDao;
import com.example.woofsyapp.model.RandomDogModel;
import com.example.woofsyapp.util.Utils;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GuessTheBreedActivity extends AppCompatActivity {
    private String dogImage, correctAnswer;
    private ImageView ivDog;
    private Context mContext;
    private ProgressBar progressBar;
    private List<String> breedsList;
    private List<String> choices;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private String c1, c2, c3, c4;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_breed);
        mContext = GuessTheBreedActivity.this;

        Intent intent = getIntent();
        dogImage = intent.getStringExtra("dogImage");

        setIds();
        setListeners();
        setChoices();
        setImage();

    }

    private void setImage() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(dogImage)
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
                        dismissLoadingDialog();
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDog);
    }

    private void setChoices() {
        breedsList = new AllBreedsDao(this).getBreedsList();
        choices = new LinkedList<>();
        String[] strArr = new String[10];

        if(dogImage != null){
            strArr = dogImage.split("/");
            correctAnswer = strArr[4];
        }else{
            callApi();
        }

        if(strArr[4] != null && !strArr[4].equals("")){
            choices.add(0, Utils.generateRandomString(breedsList));
            choices.add(0, Utils.generateRandomString(breedsList));
            choices.add(0, Utils.generateRandomString(breedsList));
            choices.add(0, strArr[4]);

            c1 = Utils.generateRandomString(choices);
            choices.remove(c1);
            c2 = Utils.generateRandomString(choices);
            choices.remove(c2);
            c3 = Utils.generateRandomString(choices);
            choices.remove(c3);
            c4 = choices.get(0);

            btnAnswer1.setText(c1);
            btnAnswer2.setText(c2);
            btnAnswer3.setText(c3);
            btnAnswer4.setText(c4);
        }

        btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
        btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
        btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
        btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
    }

    private void setIds() {
        ivDog = findViewById(R.id.iv_dog_img);
        progressBar = findViewById(R.id.progress_load_photo);
        btnAnswer1 = findViewById(R.id.btn_answer_1);
        btnAnswer2 = findViewById(R.id.btn_answer_2);
        btnAnswer3 = findViewById(R.id.btn_answer_3);
        btnAnswer4 = findViewById(R.id.btn_answer_4);
    }

    private void setListeners() {
        btnAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c1.equals(correctAnswer)){
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.red));
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showWrongAnswerDialog();
                }
            }
        });

        btnAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c2.equals(correctAnswer)){
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.red));
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showWrongAnswerDialog();
                }
            }
        });

        btnAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c3.equals(correctAnswer)){
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.red));
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showWrongAnswerDialog();
                }
            }
        });

        btnAnswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c4.equals(correctAnswer)){
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.red));
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                    showWrongAnswerDialog();
                }
            }
        });
    }

    private void callApi() {
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
                if (response.isSuccessful() && response.body() != null) {
                    dogImage = response.body().getMessage();
                    setChoices();
                    setImage();
                }
            }

            @Override
            public void onFailure(Call<RandomDogModel> call, Throwable t) {
                dismissLoadingDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCorrectAnswerDialog() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(mContext, R.style.dialog_theme_correct);
        } else {
            dialog = new Dialog(mContext);
        }
        dialog.setContentView(R.layout.dialog_correct_answer);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(mContext.getResources().getString(R.string.correct));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        TextView continueTV;
        TextView cancelTV;
        continueTV = dialog.findViewById(R.id.tv_continue);
        cancelTV = dialog.findViewById(R.id.tv_cancel);
        continueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi();
                dialog.cancel();
            }
        });
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void showWrongAnswerDialog() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(mContext, R.style.dialog_theme_wrong);
        } else {
            dialog = new Dialog(mContext);
        }

        dialog.setContentView(R.layout.dialog_wrong_answer);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(mContext.getResources().getString(R.string.incorrect));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        TextView continueTV;
        TextView cancelTV;
        continueTV = dialog.findViewById(R.id.tv_continue);
        cancelTV = dialog.findViewById(R.id.tv_cancel);
        continueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi();
                dialog.cancel();
            }
        });
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAnswer1.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                btnAnswer2.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                btnAnswer3.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                btnAnswer4.setBackgroundColor(getResources().getColor(R.color.lightest_green));
                dialog.cancel();
            //    finish();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
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
