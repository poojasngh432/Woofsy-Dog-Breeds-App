package com.dogbreeds.woofsyapp.activity;

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

import com.dogbreeds.woofsyapp.R;
import com.dogbreeds.woofsyapp.adapter.AllBreedsAdapter;
import com.dogbreeds.woofsyapp.api.Api;
import com.dogbreeds.woofsyapp.model.AllBreedsModel;
import com.dogbreeds.woofsyapp.util.Utils;

import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dogbreeds.woofsyapp.activity.MainActivity.allBreedsData;
import static com.dogbreeds.woofsyapp.activity.MainActivity.imagesList;

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
    private HashMap<String,String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_breeds);
        mContext = AllBreedsActivity.this;
        flag = -1;

        getSupportActionBar().setTitle("List Of All Breeds");
        map = new HashMap<>();
        setMap();
        setRecyclerView();
        selectedBreed = "beagle";
    }

    private void setMap() {
        map.put("affenpinscher","https://images.dog.ceo/breeds/affenpinscher/n02110627_12025.jpg");
        map.put("akita","https://images.dog.ceo/breeds/akita/512px-Akita_inu.jpeg"); map.put("african","https://images.dog.ceo/breeds/african/n02116738_1739.jpg");
        map.put("airedale","https://images.dog.ceo/breeds/airedale/n02096051_1110.jpg"); map.put("appenzeller","https://images.dog.ceo/breeds/appenzeller/n02107908_1030.jpg"); map.put("australian","https://images.dog.ceo/breeds/australian-shepherd/sadie.jpg"); map.put("basenji","https://images.dog.ceo/breeds/basenji/n02110806_1013.jpg"); map.put("beagle","https://images.dog.ceo/breeds/beagle/Joey.jpg"); map.put("bluetick","https://images.dog.ceo/breeds/bluetick/n02088632_101.jpg"); map.put("borzoi","https://images.dog.ceo/breeds/borzoi/n02090622_10281.jpg"); map.put("bouvier","https://images.dog.ceo/breeds/bouvier/n02106382_1016.jpg"); map.put("boxer","https://images.dog.ceo/breeds/boxer/n02108089_11875.jpg"); map.put("brabancon","https://images.dog.ceo/breeds/brabancon/n02112706_107.jpg"); map.put("briard","https://images.dog.ceo/breeds/briard/n02105251_12.jpg"); map.put("buhund","https://images.dog.ceo/breeds/buhund-norwegian/hakon2.jpg"); map.put("bulldog","https://images.dog.ceo/breeds/bulldog-boston/20200710_175933.jpg"); map.put("bullterrier","https://images.dog.ceo/breeds/bullterrier-staffordshire/caesar.jpg"); map.put("cairn","https://images.dog.ceo/breeds/cairn/n02096177_11012.jpg"); map.put("cattledog","https://images.dog.ceo/breeds/cattledog-australian/IMG_4421.jpg"); map.put("chihuahua","https://images.dog.ceo/breeds/chihuahua/n02085620_10976.jpg"); map.put("chow","https://images.dog.ceo/breeds/chow/n02112137_10731.jpg"); map.put("clumber","https://images.dog.ceo/breeds/clumber/n02101556_1018.jpg"); map.put("cockapoo","https://images.dog.ceo/breeds/cockapoo/Scout.jpg"); map.put("collie","https://images.dog.ceo/breeds/collie-border/brodie.jpg"); map.put("coonhound","https://images.dog.ceo/breeds/coonhound/n02089078_1668.jpg"); map.put("corgi","https://images.dog.ceo/breeds/corgi-cardigan/n02113186_1016.jpg"); map.put("cotondetulear","https://images.dog.ceo/breeds/cotondetulear/100_2397.jpg"); map.put("dachshund","https://images.dog.ceo/breeds/dachshund/Daschund_Wirehair.jpg"); map.put("dalmatian","https://images.dog.ceo/breeds/dalmatian/cooper1.jpg"); map.put("dane","https://images.dog.ceo/breeds/dane-great/dane-0.jpg"); map.put("deerhound","https://images.dog.ceo/breeds/deerhound-scottish/n02092002_11390.jpg"); map.put("dhole","https://images.dog.ceo/breeds/dhole/n02115913_1332.jpg"); map.put("dingo","https://images.dog.ceo/breeds/dingo/n02115641_10395.jpg"); map.put("doberman","https://images.dog.ceo/breeds/doberman/doberman.jpg"); map.put("elkhound","https://images.dog.ceo/breeds/elkhound-norwegian/n02091467_1679.jpg"); map.put("entlebucher","https://images.dog.ceo/breeds/entlebucher/n02108000_1011.jpg"); map.put("eskimo","https://images.dog.ceo/breeds/eskimo/n02109961_10021.jpg"); map.put("finnish","https://images.dog.ceo/breeds/finnish-lapphund/mochilamvan.jpg"); map.put("frise","https://images.dog.ceo/breeds/frise-bichon/stevebaxter_bichon_frise.jpg"); map.put("germanshepherd","https://images.dog.ceo/breeds/germanshepherd/n02106662_10338.jpg"); map.put("greyhound","https://images.dog.ceo/breeds/greyhound-italian/n02091032_10644.jpg"); map.put("groenendael","https://images.dog.ceo/breeds/groenendael/n02105056_2194.jpg"); map.put("havanese","https://images.dog.ceo/breeds/havanese/00100trPORTRAIT_00100_BURST20191222103956878_COVER.jpg"); map.put("hound","https://images.dog.ceo/breeds/hound-afghan/n02088094_12945.jpg"); map.put("husky","https://images.dog.ceo/breeds/husky/n02110185_8860.jpg"); map.put("keeshond","https://images.dog.ceo/breeds/keeshond/n02112350_1861.jpg"); map.put("kelpie","https://images.dog.ceo/breeds/kelpie/n02105412_1464.jpg"); map.put("komondor","https://images.dog.ceo/breeds/komondor/n02105505_2043.jpg"); map.put("kuvasz","https://images.dog.ceo/breeds/kuvasz/n02104029_1324.jpg"); map.put("labrador","https://images.dog.ceo/breeds/labrador/IMG_4708.jpg"); map.put("leonberg","https://images.dog.ceo/breeds/leonberg/n02111129_1178.jpg"); map.put("lhasa","https://images.dog.ceo/breeds/lhasa/n02098413_13638.jpg"); map.put("malamute","https://images.dog.ceo/breeds/malamute/n02110063_17622.jpg"); map.put("malinois","https://images.dog.ceo/breeds/malinois/n02105162_10076.jpg"); map.put("maltese","https://images.dog.ceo/breeds/maltese/n02085936_10661.jpg"); map.put("mastiff","https://images.dog.ceo/breeds/mastiff-bull/n02108422_311.jpg"); map.put("mexicanhairless","https://images.dog.ceo/breeds/mexicanhairless/n02113978_1482.jpg"); map.put("mix","https://images.dog.ceo/breeds/mix/Polo.jpg"); map.put("mountain","https://images.dog.ceo/breeds/mountain-bernese/n02107683_1224.jpg"); map.put("newfoundland","https://images.dog.ceo/breeds/newfoundland/n02111277_2865.jpg"); map.put("otterhound","https://images.dog.ceo/breeds/otterhound/n02091635_1058.jpg"); map.put("ovcharka","https://images.dog.ceo/breeds/ovcharka-caucasian/IMG_20190528_194200.jpg"); map.put("papillon","https://images.dog.ceo/breeds/papillon/n02086910_10147.jpg"); map.put("pekinese","https://images.dog.ceo/breeds/pekinese/n02086079_10159.jpg"); map.put("pembroke","https://images.dog.ceo/breeds/pembroke/n02113023_10829.jpg"); map.put("pinscher","https://images.dog.ceo/breeds/pinscher-miniature/n02107312_1586.jpg"); map.put("pitbull","https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"); map.put("pointer","https://images.dog.ceo/breeds/pointer-german/n02100236_1673.jpg"); map.put("pomeranian","https://images.dog.ceo/breeds/pomeranian/n02112018_12586.jpg"); map.put("poodle","https://images.dog.ceo/breeds/poodle-miniature/n02113712_211.jpg"); map.put("pug","https://images.dog.ceo/breeds/pug/n02110958_2777.jpg"); map.put("puggle","https://images.dog.ceo/breeds/puggle/IMG_104450.jpg"); map.put("pyrenees","https://images.dog.ceo/breeds/pyrenees/n02111500_124.jpg"); map.put("redbone","https://images.dog.ceo/breeds/redbone/n02090379_4950.jpg"); map.put("retriever","https://images.dog.ceo/breeds/retriever-chesapeake/n02099849_3326.jpg"); map.put("ridgeback","https://images.dog.ceo/breeds/ridgeback-rhodesian/n02087394_3048.jpg"); map.put("rottweiler","https://images.dog.ceo/breeds/rottweiler/n02106550_11097.jpg"); map.put("saluki","https://images.dog.ceo/breeds/saluki/n02091831_10593.jpg"); map.put("samoyed","https://images.dog.ceo/breeds/samoyed/n02111889_10084.jpg"); map.put("schipperke","https://images.dog.ceo/breeds/schipperke/n02104365_10606.jpg"); map.put("schnauzer","https://images.dog.ceo/breeds/schnauzer-giant/n02097130_1822.jpg"); map.put("setter","https://images.dog.ceo/breeds/setter-irish/n02100877_4852.jpg"); map.put("sheepdog","https://images.dog.ceo/breeds/sheepdog-english/n02105641_11253.jpg"); map.put("shiba","https://images.dog.ceo/breeds/shiba/shiba-3i.jpg"); map.put("shihtzu","https://images.dog.ceo/breeds/shihtzu/n02086240_11766.jpg"); map.put("spaniel","https://images.dog.ceo/breeds/spaniel-blenheim/n02086646_117.jpg"); map.put("springer","https://images.dog.ceo/breeds/springer-english/n02102040_403.jpg"); map.put("stbernard","https://images.dog.ceo/breeds/stbernard/n02109525_13154.jpg"); map.put("terrier","https://images.dog.ceo/breeds/terrier-american/n02093428_10381.jpg"); map.put("vizsla","https://images.dog.ceo/breeds/vizsla/n02100583_12660.jpg"); map.put("waterdog","https://images.dog.ceo/breeds/waterdog-spanish/20180723_185559.jpg"); map.put("weimaraner","https://images.dog.ceo/breeds/weimaraner/n02092339_4650.jpg"); map.put("whippet","https://images.dog.ceo/breeds/whippet/n02091134_10242.jpg"); map.put("wolfhound","https://images.dog.ceo/breeds/wolfhound-irish/n02090721_1235.jpg");
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
            adapter = new AllBreedsAdapter(allBreedsData, mContext,map);
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
