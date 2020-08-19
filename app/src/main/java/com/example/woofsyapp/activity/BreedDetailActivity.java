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
import com.example.woofsyapp.adapter.AllBreedsAdapter;
import com.example.woofsyapp.adapter.BreedTypeImagesAdapter;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.model.AllBreedsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        allImages = new AllBreedsActivity().setImagesList();

        //showLoadingDialog();
        setRecyclerView();

        Intent intent = getIntent();
        breedType = intent.getStringExtra("breedType");

        getSupportActionBar().setTitle(breedType);

    }

    private void setRecyclerView() {
        if(allImages != null){
            recyclerView = findViewById(R.id.rv_dog_breed_images);
            gridLayoutManager = new GridLayoutManager(BreedDetailActivity.this,3);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            //  adapter = new AllBreedsAdapter(allBreedsData, mContext);
            adapter = new BreedTypeImagesAdapter(allImages, mContext);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            initListener();
           // if(adapter != null && adapter.getItemCount() != 0) {
                Toast.makeText(mContext, String.valueOf(adapter.getItemCount()), Toast.LENGTH_LONG).show();
          //      dismissLoadingDialog();

        }
    }

    private void initListener(){

        adapter.setOnItemClickListener(new BreedTypeImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(BreedDetailActivity.this, DogActivity.class);

//                Article article = newsdata.get(position);
                intent.putExtra("imageAddress", allImages.get(position));
//                intent.putExtra("title", article.getTitle());
//                intent.putExtra("img",  article.getUrlToImage());
//                intent.putExtra("date",  article.getPublishedAt());
//                intent.putExtra("source",  article.getSource().getName());
//                intent.putExtra("author",  article.getAuthor());

                startActivity(intent);
            }
        });
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
