package com.example.woofsyapp.api;

import com.example.woofsyapp.model.AllBreedsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {

    String BASE_URL = "https://dog.ceo/api/";    //Root url

  //  String KEY = "wYx2JTqJKfZ_qzUI5kAf5iLzSBVQm0XiLoZUPRQBgicUajwxlvQHCTYkqYKL91SdZP3YSkiJX09K1tCGG-eKrW7ZklZ9Uspkm5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnKXFvsR88vL4WiBr168omFadgngDnj25DLpEvLRaiIpzZr1NvbW-Bo38vshdDBv10tpytj_A4aoE&lib=Mm1FD1HVuydJN5yAB3dc_e8h00DPSBbB3";
  //  String NEWS_API_KEY = "69461f79c446489e8df57822f2c8f0dd";

    @GET("breeds/list")
    Call<AllBreedsModel> getAllBreedsList();

    @GET("breed/{type}/images")
    Call<AllBreedsModel> getAllImagesOfBreed(@Path("type") String breedType);

//    @GET("countries")
//    Call<List<Countries>> getCountriesData();
//
//    @GET("macros/echo?user_content_key="+KEY)
//    Call<States> getStatesDataList();
//
//    @GET("top-headlines?country=in&apiKey="+NEWS_API_KEY)
//    Call<News> getNews();

}
