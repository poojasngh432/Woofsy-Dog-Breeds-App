package com.example.woofsyapp.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.woofsyapp.R;
import com.example.woofsyapp.adapter.DrawerItemCustomAdapter;
import com.example.woofsyapp.api.Api;
import com.example.woofsyapp.dao.AllBreedsDao;
import com.example.woofsyapp.dao.LikesDao;
import com.example.woofsyapp.database.DatabaseHelper;
import com.example.woofsyapp.model.AllBreedsModel;
import com.example.woofsyapp.model.DataModel;
import com.example.woofsyapp.model.RandomDogModel;
import com.example.woofsyapp.model.RandomGifResponse;
import com.example.woofsyapp.util.Utils;
import com.facebook.stetho.Stetho;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ImageView IV1, IV2, IV3, IV4;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ActionBarDrawerToggle mDrawerToggle;
    private Context mContext;
    public AllBreedsModel breedsData;
    public DatabaseHelper myDb;
    public String randomDog;
    private int targetMenu;
    public String randomDogImg = "";

    public static List<String> allBreedsData;
    public static List<String> imagesList;
    public static List<String> likedImages = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        getIds();
        initMethod();
        setListeners();
        setupToolbar();
        setDrawer();
        setupDrawerToggle();

        if(!Utils.isNetworkAvailable(this)){
            showToast("Not connected to Internet");
        }

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        allBreedsData = new AllBreedsDao(this).getBreedsList();

        if(allBreedsData == null || allBreedsData.size() == 0){
            if(Utils.isNetworkAvailable(this)){
                CallApiAsynTask callApiAsynTask = new CallApiAsynTask();
                callApiAsynTask.execute();
            }else{
                showToast("Not connected to Internet");
            }
        }else{
            if(Utils.isNetworkAvailable(this)){
                CallApi2AsynTask callApiAsynTask = new CallApi2AsynTask();
                callApiAsynTask.execute();
            }else{
                showToast("Not connected to Internet");
            }
        }
    }

    private void setListeners() {
        IV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetMenu = 1;
                Intent i = new Intent(MainActivity.this, AllBreedsActivity.class);
                startActivity(i);
            }
        });
        IV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetMenu = 2;
                if(Utils.isNetworkAvailable(mContext)){
                    Intent i = new Intent(MainActivity.this, GuessTheBreedActivity.class);
                    i.putExtra("dogImage", randomDog);
                    startActivity(i);
                }else{
                    showToast("Not connected to Internet.");
                }
            }
        });
        IV3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetMenu = 3;
                if(Utils.isNetworkAvailable(mContext)){
                    Intent i = new Intent(MainActivity.this, ShakeThePawActivity.class);
                    i.putExtra("dogImage",randomDog);
                    startActivity(i);
                }else{
                    showToast("Not connected to Internet.");
                }
            }
        });
        IV4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetMenu = 4;
                if(Utils.isNetworkAvailable(mContext)){
                    Intent i = new Intent(MainActivity.this, GifActivity.class);
                    i.putExtra("gifString",randomDogImg);
                    startActivity(i);
                }else{
                    showToast("Not connected to Internet.");
                }
            }
        });
    }

    private void setDrawer() {
        DataModel[] drawerItem = new DataModel[2];
        drawerItem[0] = new DataModel(R.drawable.ic_favorite, "Liked Photos");
        // drawerItem[1] = new DataModel(R.drawable.ic_favorite, "Gifs");
        drawerItem[1] = new DataModel(R.drawable.ic_info, "About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initMethod() {
        myDb = new DatabaseHelper(this);
        targetMenu = 0;
    }

    private void getIds() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        IV1 = findViewById(R.id.iv_1);
        IV2 = findViewById(R.id.iv_2);
        IV3 = findViewById(R.id.iv_3);
        IV4 = findViewById(R.id.iv_4);
    }

    class CallApiAsynTask extends AsyncTask<Void, Void, Boolean>{

        private CallApiAsynTask(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            callApiForBreeds();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean val) {
            super.onPostExecute(val);
                CallApi2AsynTask callApi2AsynTask = new CallApi2AsynTask();
                callApi2AsynTask.execute();

        }
    }

    class CallApi2AsynTask extends AsyncTask<Void, Void, Boolean>{

        private CallApi2AsynTask(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            apiGetRandomImage();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean val) {
            super.onPostExecute(val);
            if(randomDogImg == null || randomDogImg.equals("")){
                CallApi3AsynTask callApi3AsynTask = new CallApi3AsynTask();
                callApi3AsynTask.execute();
            }
        }
    }

    class CallApi3AsynTask extends AsyncTask<Void, Void, Boolean>{

        private CallApi3AsynTask(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            apiGif();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean val) {
            super.onPostExecute(val);
        }
    }

    private void callApiForBreeds() {
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
                if (response.isSuccessful() && response.body().getMessage() != null) {
                    allBreedsData = response.body().getMessage();
                    breedsData = response.body();
                    setAllBreedsData();
                    //setting up db
                    AllBreedsDao objBreedsDao = new AllBreedsDao(getApplicationContext());
                    objBreedsDao.insertBreedsLocal(allBreedsData);
                }
            }

            @Override
            public void onFailure(Call<AllBreedsModel> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void apiGetRandomImage() {
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
                    randomDog = response.body().getMessage();
                }
            }

            @Override
            public void onFailure(Call<RandomDogModel> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void apiGif(){
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
                        randomDogImg = response.body().getData().get(0).getImages().getOriginal().getUrl();

                    }
                }

                @Override
                public void onFailure(Call<RandomGifResponse> call, Throwable t) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    public static List<String> setAllBreedsData() {
        if(allBreedsData == null)
            return null;

        return allBreedsData;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        switch (position) {
            case 0:
                Intent intent = new Intent(MainActivity.this, LikesActivity.class);
                startActivity(intent);
                break;
            case 1:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    public List<String> updateLikedImages(String img, Context context){

        likedImages = new LikesDao(context).getLikesList();
        if(likedImages == null){
            likedImages = new LinkedList<>();
        }

        if(likedImages.contains(img)){
            likedImages.remove(img);
            new LikesDao(context).deleteData(img);
        }
        else{
            likedImages.add(img);
            new LikesDao(context).insertPhotoLocal(img);
        }

        return likedImages;
    }


    void showToast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
