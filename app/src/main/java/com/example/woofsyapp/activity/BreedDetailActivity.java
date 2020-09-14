package com.example.woofsyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.woofsyapp.R;
import com.example.woofsyapp.adapter.BreedTypeImagesAdapter;
import com.example.woofsyapp.util.Utils;

import java.util.List;

public class BreedDetailActivity extends AppCompatActivity {
    private String breedType;
    private BreedTypeImagesAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    public List<String> allImages;
    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_detail);
        mContext = BreedDetailActivity.this;
        getSupportActionBar().setTitle(breedType);

        Intent intent = getIntent();
        breedType = intent.getStringExtra("breedType");

        if(breedType != null){
            getSupportActionBar().setTitle(breedType);
        }

        allImages = new AllBreedsActivity().setImagesList();
        setRecyclerView();
    }

    private void setRecyclerView() {
        if(allImages != null){
            recyclerView = findViewById(R.id.rv_dog_breed_images);
            gridLayoutManager = new GridLayoutManager(BreedDetailActivity.this,3);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            adapter = new BreedTypeImagesAdapter(allImages, mContext);
            recyclerView.setAdapter(adapter);
            dismissLoadingDialog();
            adapter.notifyDataSetChanged();
            initListener();
        }
    }

    private void initListener(){

        adapter.setOnItemClickListener(new BreedTypeImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if(Utils.isNetworkAvailable(mContext)){
                    Intent intent = new Intent(BreedDetailActivity.this, DogActivity.class);

                    intent.putExtra("imageAddress", allImages.get(position));
                    intent.putExtra("breedType", breedType);

                    startActivity(intent);
                }else{
                    showToast("Not connected to Internet.");
                }
            }
        });
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
        Toast.makeText(BreedDetailActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

}
