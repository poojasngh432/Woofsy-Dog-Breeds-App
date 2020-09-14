package com.example.woofsyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.woofsyapp.R;
import com.example.woofsyapp.adapter.BreedTypeImagesAdapter;
import com.example.woofsyapp.dao.LikesDao;
import java.util.LinkedList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {

    private BreedTypeImagesAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    public List<String> allImages;
    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        mContext = LikesActivity.this;

        getSupportActionBar().setTitle("Liked Doggos");

        allImages = new LinkedList<>();
        allImages = new LikesDao(this).getLikesList();

        //showLoadingDialog();
        setRecyclerView();

    }

    private void setRecyclerView() {
        if(allImages != null){
            recyclerView = findViewById(R.id.rv_dog_breed_images);
            gridLayoutManager = new GridLayoutManager(LikesActivity.this,3);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            adapter = new BreedTypeImagesAdapter(allImages, mContext);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            initListener();
        }
    }

    private void initListener(){

        adapter.setOnItemClickListener(new BreedTypeImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent;
                if(allImages.get(position).contains("gif")){
                    intent = new Intent(LikesActivity.this, GifActivity.class);
                    intent.putExtra("gifString", allImages.get(position));
                }else{
                    intent = new Intent(LikesActivity.this, DogActivity.class);
                    intent.putExtra("imageAddress", allImages.get(position));
                }

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        allImages = new LinkedList<>();
        allImages = new LikesDao(this).getLikesList();
        setRecyclerView();
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
