package com.example.woofsyapp.api;

import com.example.woofsyapp.model.AllBreedsModel;
import com.example.woofsyapp.model.Datum;
import com.example.woofsyapp.model.RandomDogModel;
import com.example.woofsyapp.model.RandomGifResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://dog.ceo/api/";    //Root url
    String BASE_URL_GIPHY = "https://api.giphy.com/v1/gifs/";

    //Full url gif = https://api.giphy.com/v1/gifs/search?q=dog&limit=1

    @GET("breeds/list")
    Call<AllBreedsModel> getAllBreedsList();

    @GET("breeds/image/random")
    Call<RandomDogModel> getRandomDog();

    @GET("breed/{type}/images/random")
    Call<RandomDogModel> getDogImage(@Path("type") String breedType);

    @GET("breed/{type}/images")
    Call<AllBreedsModel> getAllImagesOfBreed(@Path("type") String breedType);

    @GET("search?q=dog&limit=1")
    Call<RandomGifResponse> getRandomGif(@Query("offset") int offset, @Query("api_key") String api_key);


//    @GET("countries")
//    Call<List<Countries>> getCountriesData();
//
//    @GET("macros/echo?user_content_key="+KEY)
//    Call<States> getStatesDataList();
//
//    @GET("top-headlines?country=in&apiKey="+NEWS_API_KEY)
//    Call<News> getNews();

}
