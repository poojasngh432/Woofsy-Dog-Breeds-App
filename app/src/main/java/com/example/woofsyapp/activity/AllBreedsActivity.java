package com.example.woofsyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.woofsyapp.R;
import com.example.woofsyapp.adapter.AllBreedsAdapter;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.model.AllBreedsModel;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllBreedsActivity extends AppCompatActivity {

    private AllBreedsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public List<String> allBreedsData;
    public static List<String> imagesList;
    private Context mContext;
    public AllBreedsModel breedsData;
    private SwipeRefreshLayout swipeContainer;
    private ProgressDialog progressDialog;
    private String selectedBreed;
    private Intent intent2;
    private int flag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_breeds);
        mContext = AllBreedsActivity.this;
        allBreedsData = new MainActivity().setAllBreedsData();
        flag = -1;

        getSupportActionBar().setTitle("List Of All Breeds");

        setRecyclerView();

        if(progressDialog != null)
            dismissLoadingDialog();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allBreedsData.clear();
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                callApi();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        selectedBreed = "beagle";
        callApi2();
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
        Call<AllBreedsModel> call = api.getAllBreedsList();

        call.enqueue(new Callback<AllBreedsModel>() {
            @Override
            public void onResponse(Call<AllBreedsModel> call, Response<AllBreedsModel> response) {
                dismissLoadingDialog();
                if (response.isSuccessful() && response.body().getMessage() != null) {
                    allBreedsData = response.body().getMessage();
                    breedsData = response.body();
                    setRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<AllBreedsModel> call, Throwable t) {
                dismissLoadingDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static List<String> setImagesList() {
        if(imagesList == null)
            return null;

        return imagesList;
    }

    private void setRecyclerView() {
        if(allBreedsData != null){
            adapter = new AllBreedsAdapter(allBreedsData, mContext);
            recyclerView = findViewById(R.id.rv_dog_breeds);
            layoutManager = new GridLayoutManager(AllBreedsActivity.this,2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            initListener();
        }
    }

    private void initListener(){

        adapter.setOnItemClickListener(new AllBreedsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                intent2 = new Intent(AllBreedsActivity.this, BreedDetailActivity.class);
                intent2.putExtra("breedType", allBreedsData.get(position));
                selectedBreed = allBreedsData.get(position);
                flag = 0;
                callApi2();
                setImagesList();

            }
        });
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
        Call<AllBreedsModel> call = api.getAllImagesOfBreed(selectedBreed);

        call.enqueue(new Callback<AllBreedsModel>() {
            @Override
            public void onResponse(Call<AllBreedsModel> call, Response<AllBreedsModel> response) {
                if (response.isSuccessful() && response.body().getMessage() != null) {
                    imagesList = response.body().getMessage();
                    if(flag == 0){
                        startActivity(intent2);
                    }
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<AllBreedsModel> call, Throwable t) {
                dismissLoadingDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
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
