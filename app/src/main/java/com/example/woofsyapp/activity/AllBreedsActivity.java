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
import android.widget.Toast;

import com.example.woofsyapp.R;
import com.example.woofsyapp.adapter.AllBreedsAdapter;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.model.AllBreedsModel;
import com.example.woofsyapp.util.Utils;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.woofsyapp.activity.MainActivity.allBreedsData;
import static com.example.woofsyapp.activity.MainActivity.imagesList;

public class AllBreedsActivity extends AppCompatActivity {

    private AllBreedsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context mContext;
    //private SwipeRefreshLayout swipeContainer;
    private ProgressDialog progressDialog;
    private String selectedBreed;
    private Intent intent2;
    private int flag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_breeds);
        mContext = AllBreedsActivity.this;
        flag = -1;

        getSupportActionBar().setTitle("List Of All Breeds");
        setRecyclerView();
        selectedBreed = "beagle";
    }

    public List<String> setImagesList() {
        if(imagesList == null){
            callApiForImages();
        }

        return imagesList;
    }

    private void callApiForImages() {
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
                }
            }

            @Override
            public void onFailure(Call<AllBreedsModel> call, Throwable t) {
             //   Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
                if(Utils.isNetworkAvailable(mContext)){
                    intent2 = new Intent(AllBreedsActivity.this, BreedDetailActivity.class);
                    if(allBreedsData != null){
                        intent2.putExtra("breedType", allBreedsData.get(position));
                        selectedBreed = allBreedsData.get(position);
                    }
                    flag = 0;
                    callApiForImages();
                }else{
                    showToast("Not connected to Internet.");
                }
            }
        });
    }

    private void showLoadingDialog() {
        progressDialog = ProgressDialog.show(mContext, null, this.getString(R.string.loading), false, false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dismissLoadingDialog();
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
        Toast.makeText(AllBreedsActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

}
