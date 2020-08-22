package com.example.woofsyapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.woofsyapp.R;
import com.example.woofsyapp.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class GuessTheBreedActivity extends AppCompatActivity {
    private String dogImage, correctAnswer;
    private ImageView ivDog;
    private Context mContext;
    private ProgressBar progressBar;
    private List<String> breedsList;
    private List<String> choices;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private String c1, c2, c3, c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_breed);
        ivDog = findViewById(R.id.iv_dog_img);
        progressBar = findViewById(R.id.progress_load_photo);
        btnAnswer1 = findViewById(R.id.btn_answer_1);
        btnAnswer2 = findViewById(R.id.btn_answer_2);
        btnAnswer3 = findViewById(R.id.btn_answer_3);
        btnAnswer4 = findViewById(R.id.btn_answer_4);

        mContext = GuessTheBreedActivity.this;

        dogImage = new MainActivity().getRandomDogImage();
        breedsList = new MainActivity().setAllBreedsData();
        choices = new LinkedList<>();

        String[] strArr = dogImage.split("/");

        correctAnswer = strArr[4];

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
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDog);

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

        btnAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(c1.equals(correctAnswer)){
                   btnAnswer1.setBackgroundColor(getResources().getColor(R.color.end_marker));
                   showCorrectAnswerDialog();
               }else{
                   btnAnswer1.setBackgroundColor(getResources().getColor(R.color.red));
                   showWrongAnswerDialog();
               }
            }
        });

        btnAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c2.equals(correctAnswer)){
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer2.setBackgroundColor(getResources().getColor(R.color.red));
                    showWrongAnswerDialog();
                }
            }
        });

        btnAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c3.equals(correctAnswer)){
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer3.setBackgroundColor(getResources().getColor(R.color.red));
                    showWrongAnswerDialog();
                }
            }
        });

        btnAnswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c4.equals(correctAnswer)){
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.end_marker));
                    showCorrectAnswerDialog();
                }else{
                    btnAnswer4.setBackgroundColor(getResources().getColor(R.color.red));
                    showWrongAnswerDialog();
                }
            }
        });

    }

    private void showCorrectAnswerDialog() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(mContext, R.style.AppTheme);
        } else {
            dialog = new Dialog(mContext);
        }
        dialog.setContentView(R.layout.dialog_correct_answer);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 10, (4 * height) / 5);
        dialog.setTitle(mContext.getResources().getString(R.string.correct));
        dialog.getWindow().setGravity(Gravity.END);
        dialog.getWindow().getAttributes().windowAnimations = R.style.MaterialAlertDialog_MaterialComponents_Title_Text;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void showWrongAnswerDialog() {
        final Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(mContext, R.style.AppTheme);
        } else {
            dialog = new Dialog(mContext);
        }
        dialog.setContentView(R.layout.dialog_wrong_answer);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 10, (4 * height) / 5);
        dialog.setTitle(mContext.getResources().getString(R.string.incorrect));
        dialog.getWindow().setGravity(Gravity.END);
        dialog.getWindow().getAttributes().windowAnimations = R.style.MaterialAlertDialog_MaterialComponents_Title_Text;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setCancelable(true);
        dialog.show();
    }



}
